package com.ops.hunting.analytics.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ops.hunting.analytics.dto.DetectionAnalyticDto;
import com.ops.hunting.analytics.entity.AnalyticExecution;
import com.ops.hunting.analytics.entity.DetectionAnalytic;
import com.ops.hunting.analytics.repository.DetectionAnalyticRepository;
import com.ops.hunting.analytics.service.platform.PlatformService;
import com.ops.hunting.common.enums.Platform;

@Service
@Transactional
public class DetectionAnalyticService {

	private final DetectionAnalyticRepository analyticRepository;
	private final Map<Platform, PlatformService> platformServices;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	public DetectionAnalyticService(DetectionAnalyticRepository analyticRepository,
			List<PlatformService> platformServiceList, KafkaTemplate<String, Object> kafkaTemplate) {
		this.analyticRepository = analyticRepository;
		this.platformServices = platformServiceList.stream()
				.collect(Collectors.toMap(PlatformService::getSupportedPlatform, service -> service));
		this.kafkaTemplate = kafkaTemplate;
	}

	@Transactional
	public DetectionAnalyticDto createAnalytic(DetectionAnalyticDto dto) {
		DetectionAnalytic entity = convertToEntity(dto);
		DetectionAnalytic saved = analyticRepository.save(entity);

		// Deploy to platform
		try {
			deployToPlatform(saved);
			publishAnalyticEvent(saved, "ANALYTIC_CREATED");
		} catch (Exception e) {
			throw new RuntimeException("Failed to deploy analytic to platform: " + e.getMessage());
		}

		return convertToDto(saved);
	}

	@Cacheable(value = "analytics", key = "#id")
	public Optional<DetectionAnalyticDto> getAnalyticById(String id) {
		return analyticRepository.findById(id).map(this::convertToDto);
	}

	public Page<DetectionAnalyticDto> getAllAnalytics(Pageable pageable) {
		return analyticRepository.findAll(pageable).map(this::convertToDto);
	}

	public Page<DetectionAnalyticDto> getActiveAnalytics(Pageable pageable) {
		return analyticRepository.findByIsActiveTrue(pageable).map(this::convertToDto);
	}

	public Page<DetectionAnalyticDto> searchAnalytics(String search, Pageable pageable) {
		return analyticRepository.searchAnalytics(search, pageable).map(this::convertToDto);
	}

	public List<DetectionAnalyticDto> getAnalyticsByPlatform(Platform platform) {
		return analyticRepository.findByPlatform(platform).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<DetectionAnalyticDto> getAnalyticsByCreator(String creator) {
		return analyticRepository.findByCreatedBy(creator).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<DetectionAnalyticDto> getAnalyticsByThreatIntelligence(String threatIntelligenceId) {
		return analyticRepository.findByThreatIntelligenceId(threatIntelligenceId).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<DetectionAnalyticDto> getHighAccuracyAnalytics(BigDecimal minAccuracy) {
		return analyticRepository.findByAccuracyGreaterThanEqual(minAccuracy).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@CacheEvict(value = "analytics", key = "#id")
	@Transactional
	public DetectionAnalyticDto updateAnalytic(String id, DetectionAnalyticDto dto) {
		Optional<DetectionAnalytic> existingOpt = analyticRepository.findById(id);
		if (existingOpt.isEmpty()) {
			throw new RuntimeException("Analytic not found with id: " + id);
		}

		DetectionAnalytic existing = existingOpt.get();
		existing.setName(dto.getName());
		existing.setDescription(dto.getDescription());
		existing.setQueryText(dto.getQueryText());
		existing.setPlatform(dto.getPlatform());
		existing.setAccuracy(dto.getAccuracy());
		existing.setThreatIntelligenceId(dto.getThreatIntelligenceId());
		existing.setLastModified(LocalDateTime.now());

		DetectionAnalytic updated = analyticRepository.save(existing);

		// Redeploy to platform if active
		if (updated.getIsActive()) {
			try {
				deployToPlatform(updated);
				publishAnalyticEvent(updated, "ANALYTIC_UPDATED");
			} catch (Exception e) {
				throw new RuntimeException("Failed to redeploy updated analytic: " + e.getMessage());
			}
		}

		return convertToDto(updated);
	}

	@CacheEvict(value = "analytics", key = "#id")
	@Transactional
	public DetectionAnalyticDto activateAnalytic(String id) {
		Optional<DetectionAnalytic> analyticOpt = analyticRepository.findById(id);
		if (analyticOpt.isEmpty()) {
			throw new RuntimeException("Analytic not found with id: " + id);
		}

		DetectionAnalytic analytic = analyticOpt.get();
		analytic.setIsActive(true);
		DetectionAnalytic updated = analyticRepository.save(analytic);

		try {
			deployToPlatform(updated);
			publishAnalyticEvent(updated, "ANALYTIC_ACTIVATED");
		} catch (Exception e) {
			throw new RuntimeException("Failed to activate analytic: " + e.getMessage());
		}

		return convertToDto(updated);
	}

	@CacheEvict(value = "analytics", key = "#id")
	@Transactional
	public DetectionAnalyticDto deactivateAnalytic(String id) {
		Optional<DetectionAnalytic> analyticOpt = analyticRepository.findById(id);
		if (analyticOpt.isEmpty()) {
			throw new RuntimeException("Analytic not found with id: " + id);
		}

		DetectionAnalytic analytic = analyticOpt.get();
		analytic.setIsActive(false);
		DetectionAnalytic updated = analyticRepository.save(analytic);

		try {
			undeployFromPlatform(updated);
			publishAnalyticEvent(updated, "ANALYTIC_DEACTIVATED");
		} catch (Exception e) {
			// Log but don't fail - deactivation should succeed even if platform fails
			System.err.println("Failed to undeploy analytic from platform: " + e.getMessage());
		}

		return convertToDto(updated);
	}

	@Transactional
	public void executeAnalytic(UUID uuid) {
		Optional<DetectionAnalytic> analyticOpt = analyticRepository.findById(uuid.toString());
		if (analyticOpt.isEmpty()) {
			throw new RuntimeException("Analytic not found with id: " + uuid.toString());
		}

		DetectionAnalytic analytic = analyticOpt.get();
		if (!analytic.getIsActive()) {
			throw new RuntimeException("Cannot execute inactive analytic");
		}

		PlatformService platformService = platformServices.get(analytic.getPlatform());
		if (platformService == null) {
			throw new RuntimeException("No platform service available for: " + analytic.getPlatform());
		}

		AnalyticExecution execution = new AnalyticExecution(analytic, LocalDateTime.now());

		try {
			long startTime = System.currentTimeMillis();
			var result = platformService.executeQuery(analytic.getQueryText());
			long duration = System.currentTimeMillis() - startTime;

			execution.markAsSuccessful(duration, result.getRecordsProcessed(), result.getAlertsGenerated());
			analytic.incrementExecutionCount();

			if (result.getAlertsGenerated() > 0) {
				// Publish alerts to alert management service
				publishAlertsGenerated(analytic, result);
			}

		} catch (Exception e) {
			execution.markAsFailed(e.getMessage());
		}

		analytic.getExecutions().add(execution);
		analyticRepository.save(analytic);
	}

	@Transactional
	public void executeAllActiveAnalytics() {
		List<DetectionAnalytic> activeAnalytics = analyticRepository.findByIsActiveTrue();
		for (DetectionAnalytic analytic : activeAnalytics) {
			try {
				executeAnalytic(analytic.getId());
			} catch (Exception e) {
				System.err.println("Failed to execute analytic " + analytic.getId() + ": " + e.getMessage());
			}
		}
	}

	public List<DetectionAnalyticDto> getStaleAnalytics(int hours) {
		LocalDateTime threshold = LocalDateTime.now().minusHours(hours);
		return analyticRepository.findStaleAnalytics(threshold).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<Object[]> getPlatformStatistics() {
		return analyticRepository.countByPlatform();
	}

	public List<Object[]> getCreatorStatistics() {
		return analyticRepository.countByCreator();
	}

	public long getActiveAnalyticsCount() {
		return analyticRepository.countActiveAnalytics();
	}

	public BigDecimal getAverageAccuracy() {
		return analyticRepository.getAverageAccuracy();
	}

	private void deployToPlatform(DetectionAnalytic analytic) {
		PlatformService platformService = platformServices.get(analytic.getPlatform());
		if (platformService != null) {
			platformService.deployAnalytic(analytic.getId(), analytic.getQueryText());
		}
	}

	private void undeployFromPlatform(DetectionAnalytic analytic) {
		PlatformService platformService = platformServices.get(analytic.getPlatform());
		if (platformService != null) {
			platformService.undeployAnalytic(analytic.getId());
		}
	}

	private void publishAnalyticEvent(DetectionAnalytic analytic, String eventType) {
		try {
			DetectionAnalyticDto dto = convertToDto(analytic);
			AnalyticEvent event = new AnalyticEvent(eventType, dto);
			kafkaTemplate.send("analytic-events", eventType, event);
		} catch (Exception e) {
			System.err.println("Failed to publish analytic event: " + e.getMessage());
		}
	}

	private void publishAlertsGenerated(DetectionAnalytic analytic, PlatformService.QueryResult result) {
		try {
			AlertGeneratedEvent event = new AlertGeneratedEvent(analytic.getId(), result.getAlertsGenerated(),
					result.getAlertData());
			kafkaTemplate.send("alert-events", "ALERTS_GENERATED", event);
		} catch (Exception e) {
			System.err.println("Failed to publish alerts generated event: " + e.getMessage());
		}
	}

	private DetectionAnalytic convertToEntity(DetectionAnalyticDto dto) {
		DetectionAnalytic entity = new DetectionAnalytic();
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setQueryText(dto.getQueryText());
		entity.setPlatform(dto.getPlatform());
		entity.setCreatedBy(dto.getCreatedBy());
		entity.setAccuracy(dto.getAccuracy());
		entity.setThreatIntelligenceId(dto.getThreatIntelligenceId());
		entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
		return entity;
	}

	private DetectionAnalyticDto convertToDto(DetectionAnalytic entity) {
		DetectionAnalyticDto dto = new DetectionAnalyticDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setDescription(entity.getDescription());
		dto.setQueryText(entity.getQueryText());
		dto.setPlatform(entity.getPlatform());
		dto.setCreatedBy(entity.getCreatedBy());
		dto.setLastModified(entity.getLastModified());
		dto.setIsActive(entity.getIsActive());
		dto.setAccuracy(entity.getAccuracy());
		dto.setThreatIntelligenceId(entity.getThreatIntelligenceId());
		dto.setExecutionCount(entity.getExecutionCount());
		dto.setLastExecuted(entity.getLastExecuted());
		dto.setAlertCount(entity.getAlertCount());
		dto.setFalsePositiveCount(entity.getFalsePositiveCount());
		dto.setFalsePositiveRate(entity.getFalsePositiveRate());
		// dto.setCreatedDate(entity.getCreatedDate());
		return dto;
	}

	// Inner classes for events
	public static class AnalyticEvent {
		private String eventType;
		private DetectionAnalyticDto data;
		private LocalDateTime timestamp;

		public AnalyticEvent(String eventType, DetectionAnalyticDto data) {
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

		public DetectionAnalyticDto getData() {
			return data;
		}

		public void setData(DetectionAnalyticDto data) {
			this.data = data;
		}

		public LocalDateTime getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
		}
	}

	public static class AlertGeneratedEvent {
		private UUID analyticId;
		private int alertCount;
		private String alertData;
		private LocalDateTime timestamp;

		public AlertGeneratedEvent(UUID uuid, int alertCount, String alertData) {
			this.analyticId = uuid;
			this.alertCount = alertCount;
			this.alertData = alertData;
			this.timestamp = LocalDateTime.now();
		}

		// Getters and setters
		public UUID getAnalyticId() {
			return analyticId;
		}

		public void setAnalyticId(UUID analyticId) {
			this.analyticId = analyticId;
		}

		public int getAlertCount() {
			return alertCount;
		}

		public void setAlertCount(int alertCount) {
			this.alertCount = alertCount;
		}

		public String getAlertData() {
			return alertData;
		}

		public void setAlertData(String alertData) {
			this.alertData = alertData;
		}

		public LocalDateTime getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
		}
	}
}
