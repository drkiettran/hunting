package com.ops.hunting.threatintel.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ops.hunting.common.enums.SeverityLevel;
import com.ops.hunting.common.enums.ThreatType;
import com.ops.hunting.threatintel.dto.ThreatIntelligenceDto;
import com.ops.hunting.threatintel.entity.ThreatIntelligence;
import com.ops.hunting.threatintel.repository.ThreatIntelligenceRepository;

@Service
@Transactional
public class ThreatIntelligenceService {

	private final ThreatIntelligenceRepository threatIntelligenceRepository;
	private final IndicatorService indicatorService;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	public ThreatIntelligenceService(ThreatIntelligenceRepository threatIntelligenceRepository,
			IndicatorService indicatorService, KafkaTemplate<String, Object> kafkaTemplate) {
		this.threatIntelligenceRepository = threatIntelligenceRepository;
		this.indicatorService = indicatorService;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Transactional
	public ThreatIntelligenceDto createThreatIntelligence(ThreatIntelligenceDto dto) {
		ThreatIntelligence entity = convertToEntity(dto);
		ThreatIntelligence saved = threatIntelligenceRepository.save(entity);

		// Publish event for high priority threats
		if (saved.isHighPriority()) {
			publishThreatIntelligenceEvent(saved, "NEW_HIGH_PRIORITY_THREAT");
		}

		// Publish general threat intelligence event
		publishThreatIntelligenceEvent(saved, "NEW_THREAT_INTELLIGENCE");

		return convertToDto(saved);
	}

	@Cacheable(value = "threat-intelligence", key = "#id")
	public Optional<ThreatIntelligenceDto> getThreatIntelligenceById(String id) {
		return threatIntelligenceRepository.findById(id).map(this::convertToDto);
	}

	public Page<ThreatIntelligenceDto> getAllThreatIntelligence(Pageable pageable) {
		return threatIntelligenceRepository.findAll(pageable).map(this::convertToDto);
	}

	public Page<ThreatIntelligenceDto> searchThreatIntelligence(String search, Pageable pageable) {
		return threatIntelligenceRepository.searchThreatIntelligence(search, pageable).map(this::convertToDto);
	}

	public List<ThreatIntelligenceDto> getThreatIntelligenceByType(ThreatType threatType) {
		return threatIntelligenceRepository.findByThreatType(threatType).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<ThreatIntelligenceDto> getThreatIntelligenceBySeverity(SeverityLevel severity) {
		return threatIntelligenceRepository.findBySeverity(severity).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<ThreatIntelligenceDto> getThreatIntelligenceBySource(String source) {
		return threatIntelligenceRepository.findBySource(source).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<ThreatIntelligenceDto> getThreatIntelligenceByDateRange(LocalDateTime startDate,
			LocalDateTime endDate) {
		return threatIntelligenceRepository.findByDiscoveredDateBetween(startDate, endDate).stream()
				.map(this::convertToDto).collect(Collectors.toList());
	}

	public List<ThreatIntelligenceDto> getHighPriorityThreatIntelligence() {
		List<SeverityLevel> highPrioritySeverities = List.of(SeverityLevel.HIGH, SeverityLevel.CRITICAL);
		return threatIntelligenceRepository.findBySeverityIn(highPrioritySeverities).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@CacheEvict(value = "threat-intelligence", key = "#id")
	@Transactional
	public ThreatIntelligenceDto updateThreatIntelligence(String id, ThreatIntelligenceDto dto) {
		Optional<ThreatIntelligence> existingOpt = threatIntelligenceRepository.findById(id);
		if (existingOpt.isEmpty()) {
			throw new RuntimeException("Threat intelligence not found with id: " + id);
		}

		ThreatIntelligence existing = existingOpt.get();
		existing.setSource(dto.getSource());
		existing.setThreatType(dto.getThreatType());
		existing.setTtp(dto.getTtp());
		existing.setDescription(dto.getDescription());
		existing.setSeverity(dto.getSeverity());
		existing.setDiscoveredDate(dto.getDiscoveredDate());
		existing.setReportedBy(dto.getReportedBy());

		ThreatIntelligence updated = threatIntelligenceRepository.save(existing);

		// Publish update event
		publishThreatIntelligenceEvent(updated, "THREAT_INTELLIGENCE_UPDATED");

		return convertToDto(updated);
	}

	@CacheEvict(value = "threat-intelligence", key = "#id")
	@Transactional
	public void deleteThreatIntelligence(String id) {
		if (!threatIntelligenceRepository.existsById(id)) {
			throw new RuntimeException("Threat intelligence not found with id: " + id);
		}
		threatIntelligenceRepository.deleteById(id);

		// Publish deletion event
		kafkaTemplate.send("threat-intelligence-events", "THREAT_INTELLIGENCE_DELETED", id);
	}

	public long getRecentThreatIntelligenceCount(int days) {
		LocalDateTime since = LocalDateTime.now().minusDays(days);
		return threatIntelligenceRepository.countByDiscoveredDateAfter(since);
	}

	public List<Object[]> getThreatTypeStatistics() {
		return threatIntelligenceRepository.countByThreatType();
	}

	public List<Object[]> getSeverityStatistics() {
		return threatIntelligenceRepository.countBySeverity();
	}

	private void publishThreatIntelligenceEvent(ThreatIntelligence threatIntelligence, String eventType) {
		try {
			ThreatIntelligenceDto dto = convertToDto(threatIntelligence);
			ThreatIntelligenceEvent event = new ThreatIntelligenceEvent(eventType, dto);
			kafkaTemplate.send("threat-intelligence-events", eventType, event);
		} catch (Exception e) {
			// Log error but don't fail the main operation
			System.err.println("Failed to publish threat intelligence event: " + e.getMessage());
		}
	}

	private ThreatIntelligence convertToEntity(ThreatIntelligenceDto dto) {
		ThreatIntelligence entity = new ThreatIntelligence();
		entity.setSource(dto.getSource());
		entity.setThreatType(dto.getThreatType());
		entity.setTtp(dto.getTtp());
		entity.setDescription(dto.getDescription());
		entity.setSeverity(dto.getSeverity());
		entity.setDiscoveredDate(dto.getDiscoveredDate());
		entity.setReportedBy(dto.getReportedBy());
		return entity;
	}

	private ThreatIntelligenceDto convertToDto(ThreatIntelligence entity) {
		ThreatIntelligenceDto dto = new ThreatIntelligenceDto();
		dto.setId(entity.getId());
		dto.setSource(entity.getSource());
		dto.setThreatType(entity.getThreatType());
		dto.setTtp(entity.getTtp());
		dto.setDescription(entity.getDescription());
		dto.setSeverity(entity.getSeverity());
		dto.setDiscoveredDate(entity.getDiscoveredDate());
		dto.setReportedBy(entity.getReportedBy());
		dto.setCreatedDate(entity.getCreatedDate());
		dto.setUpdatedDate(entity.getUpdatedDate());

		// Load indicators if needed
		if (entity.getIndicators() != null && !entity.getIndicators().isEmpty()) {
			dto.setIndicators(entity.getIndicators().stream()
					.map(ti -> indicatorService.convertToDto(ti.getIndicator())).collect(Collectors.toList()));
		}

		return dto;
	}

	// Inner class for Kafka events
	public static class ThreatIntelligenceEvent {
		private String eventType;
		private ThreatIntelligenceDto data;
		private LocalDateTime timestamp;

		public ThreatIntelligenceEvent(String eventType, ThreatIntelligenceDto data) {
			this.eventType = eventType;
			this.data = data;
			this.timestamp = LocalDateTime.now();
		}

		// Getters and setters
		public String getEventType() {
			return eventType;
		}

		public void setEventType(String eventType) {
			this.eventType = eventType;
		}

		public ThreatIntelligenceDto getData() {
			return data;
		}

		public void setData(ThreatIntelligenceDto data) {
			this.data = data;
		}

		public LocalDateTime getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
		}
	}
}