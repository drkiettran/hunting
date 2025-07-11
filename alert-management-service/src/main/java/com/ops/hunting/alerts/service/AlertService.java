package com.ops.hunting.alerts.service;

import java.math.BigDecimal;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ops.hunting.alerts.dto.AlertCreateDto;
import com.ops.hunting.alerts.dto.AlertUpdateDto;
import com.ops.hunting.alerts.entity.Alert;
import com.ops.hunting.alerts.entity.AlertIndicator;
import com.ops.hunting.alerts.repository.AlertRepository;
import com.ops.hunting.common.dto.AlertDto;
import com.ops.hunting.common.enums.AlertStatus;
import com.ops.hunting.common.enums.SeverityLevel;

@Service
@Transactional
public class AlertService {

	private final AlertRepository alertRepository;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final SimpMessagingTemplate messagingTemplate;

	@Autowired
	public AlertService(AlertRepository alertRepository, KafkaTemplate<String, Object> kafkaTemplate,
			SimpMessagingTemplate messagingTemplate) {
		this.alertRepository = alertRepository;
		this.kafkaTemplate = kafkaTemplate;
		this.messagingTemplate = messagingTemplate;
	}

	@Transactional
	public AlertDto createAlert(AlertCreateDto createDto) {
		Alert alert = new Alert();
		alert.setTimestamp(createDto.getTimestamp());
		alert.setSeverity(createDto.getSeverity());
		alert.setDescription(createDto.getDescription());
		alert.setRawData(createDto.getRawData());
		alert.setAnalyticId(createDto.getAnalyticId());
		alert.setStatus(AlertStatus.NEW);
		alert.setFalsePositive(false);

		// Add indicators if provided
		if (createDto.getIndicatorIds() != null && !createDto.getIndicatorIds().isEmpty()) {
			List<AlertIndicator> indicators = createDto.getIndicatorIds().stream()
					.map(indicatorId -> new AlertIndicator(alert, indicatorId, new BigDecimal("0.8")))
					.collect(Collectors.toList());
			alert.setIndicators(indicators);
		}

		Alert saved = alertRepository.save(alert);

		// Publish alert event
		publishAlertEvent(saved, "ALERT_CREATED");

		// Send real-time notification for high priority alerts
		if (saved.isHighPriority()) {
			sendRealTimeNotification(saved);
		}

		return convertToDto(saved);
	}

	@Cacheable(value = "alerts", key = "#id")
	public Optional<AlertDto> getAlertById(String id) {
		return alertRepository.findById(id).map(this::convertToDto);
	}

	public Page<AlertDto> getAllAlerts(Pageable pageable) {
		return alertRepository.findAll(pageable).map(this::convertToDto);
	}

	public Page<AlertDto> getActiveAlerts(Pageable pageable) {
		return alertRepository.findActiveAlerts(pageable).map(this::convertToDto);
	}

	public Page<AlertDto> searchAlerts(String search, Pageable pageable) {
		return alertRepository.searchAlerts(search, pageable).map(this::convertToDto);
	}

	public Page<AlertDto> getAlertsByStatus(AlertStatus status, Pageable pageable) {
		return alertRepository.findByStatus(status, pageable).map(this::convertToDto);
	}

	public Page<AlertDto> getAlertsBySeverity(SeverityLevel severity, Pageable pageable) {
		return alertRepository.findBySeverity(severity, pageable).map(this::convertToDto);
	}

	public Page<AlertDto> getAlertsByAssignedTo(String assignedTo, Pageable pageable) {
		return alertRepository.findByAssignedTo(assignedTo, pageable).map(this::convertToDto);
	}

	public List<AlertDto> getAlertsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
		return alertRepository.findByTimestampBetween(startDate, endDate).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<AlertDto> getHighPriorityAlerts() {
		List<SeverityLevel> highPrioritySeverities = List.of(SeverityLevel.HIGH, SeverityLevel.CRITICAL);
		List<AlertStatus> activeStatuses = List.of(AlertStatus.NEW, AlertStatus.ASSIGNED, AlertStatus.IN_PROGRESS);
		return alertRepository.findBySeverityInAndStatusIn(highPrioritySeverities, activeStatuses).stream()
				.map(this::convertToDto).collect(Collectors.toList());
	}

	@CacheEvict(value = "alerts", key = "#id")
	@Transactional
	public AlertDto updateAlert(String id, AlertUpdateDto updateDto) {
		Optional<Alert> alertOpt = alertRepository.findById(id);
		if (alertOpt.isEmpty()) {
			throw new RuntimeException("Alert not found with id: " + id);
		}

		Alert alert = alertOpt.get();
		AlertStatus previousStatus = alert.getStatus();

		if (updateDto.getSeverity() != null) {
			alert.setSeverity(updateDto.getSeverity());
		}
		if (updateDto.getStatus() != null) {
			alert.setStatus(updateDto.getStatus());
		}
		if (updateDto.getDescription() != null) {
			alert.setDescription(updateDto.getDescription());
		}
		if (updateDto.getAssignedTo() != null) {
			alert.setAssignedTo(updateDto.getAssignedTo());
		}
		if (updateDto.getResolutionNotes() != null) {
			alert.setResolutionNotes(updateDto.getResolutionNotes());
		}

		// Update resolved date if status changed to resolved
		if (alert.isResolved() && !previousStatus.equals(alert.getStatus())) {
			alert.setResolvedDate(LocalDateTime.now());
		}

		Alert updated = alertRepository.save(alert);

		// Publish update event
		publishAlertEvent(updated, "ALERT_UPDATED");

		return convertToDto(updated);
	}

	@CacheEvict(value = "alerts", key = "#id")
	@Transactional
	public AlertDto assignAlert(String id, String analystId) {
		Optional<Alert> alertOpt = alertRepository.findById(id);
		if (alertOpt.isEmpty()) {
			throw new RuntimeException("Alert not found with id: " + id);
		}

		Alert alert = alertOpt.get();
		alert.assignTo(analystId);
		Alert updated = alertRepository.save(alert);

		// Publish assignment event
		publishAlertEvent(updated, "ALERT_ASSIGNED");

		return convertToDto(updated);
	}

	@CacheEvict(value = "alerts", key = "#id")
	@Transactional
	public AlertDto markInProgress(String id) {
		Optional<Alert> alertOpt = alertRepository.findById(id);
		if (alertOpt.isEmpty()) {
			throw new RuntimeException("Alert not found with id: " + id);
		}

		Alert alert = alertOpt.get();
		alert.markAsInProgress();
		Alert updated = alertRepository.save(alert);

		// Publish status change event
		publishAlertEvent(updated, "ALERT_IN_PROGRESS");

		return convertToDto(updated);
	}

	@CacheEvict(value = "alerts", key = "#id")
	@Transactional
	public AlertDto resolveAlert(String id, String resolutionNotes) {
		Optional<Alert> alertOpt = alertRepository.findById(id);
		if (alertOpt.isEmpty()) {
			throw new RuntimeException("Alert not found with id: " + id);
		}

		Alert alert = alertOpt.get();
		alert.resolve(resolutionNotes);
		Alert updated = alertRepository.save(alert);

		// Publish resolution event
		publishAlertEvent(updated, "ALERT_RESOLVED");

		return convertToDto(updated);
	}

	@CacheEvict(value = "alerts", key = "#id")
	@Transactional
	public AlertDto markAsFalsePositive(String id, String notes) {
		Optional<Alert> alertOpt = alertRepository.findById(id);
		if (alertOpt.isEmpty()) {
			throw new RuntimeException("Alert not found with id: " + id);
		}

		Alert alert = alertOpt.get();
		alert.markAsFalsePositive(notes);
		Alert updated = alertRepository.save(alert);

		// Publish false positive event
		publishAlertEvent(updated, "ALERT_FALSE_POSITIVE");

		return convertToDto(updated);
	}

	public long getAlertCountByStatus(AlertStatus status) {
		return alertRepository.countByStatus(status);
	}

	public long getRecentCriticalAlertCount(int hours) {
		LocalDateTime since = LocalDateTime.now().minusHours(hours);
		return alertRepository.countBySeverityAndTimestampAfter(SeverityLevel.CRITICAL, since);
	}

	public List<Object[]> getAlertStatusStatistics() {
		return alertRepository.countByStatus();
	}

	public List<Object[]> getAlertSeverityStatistics() {
		return alertRepository.countBySeverity();
	}

	public List<Object[]> getDailyAlertStatistics(int days) {
		LocalDateTime since = LocalDateTime.now().minusDays(days);
		return alertRepository.countDailyAlerts(since);
	}

	public List<Object[]> getAnalystWorkloadStatistics() {
		return alertRepository.countByAssignedAnalyst();
	}

	private void publishAlertEvent(Alert alert, String eventType) {
		try {
			AlertDto dto = convertToDto(alert);
			AlertEvent event = new AlertEvent(eventType, dto);
			kafkaTemplate.send("alert-events", eventType, event);
		} catch (Exception e) {
			// Log error but don't fail the main operation
			System.err.println("Failed to publish alert event: " + e.getMessage());
		}
	}

	private void sendRealTimeNotification(Alert alert) {
		try {
			AlertDto dto = convertToDto(alert);
			messagingTemplate.convertAndSend("/topic/alerts/high-priority", dto);
		} catch (Exception e) {
			// Log error but don't fail the main operation
			System.err.println("Failed to send real-time notification: " + e.getMessage());
		}
	}

	private AlertDto convertToDto(Alert alert) {
		AlertDto dto = new AlertDto();
		dto.setId(alert.getId());
		dto.setTimestamp(alert.getTimestamp());
		dto.setSeverity(alert.getSeverity());
		dto.setStatus(alert.getStatus());
		dto.setDescription(alert.getDescription());
		dto.setRawData(alert.getRawData());
		dto.setAssignedTo(alert.getAssignedTo());
		dto.setFalsePositive(alert.getFalsePositive());
		dto.setAnalyticId(alert.getAnalyticId());
		dto.setCreatedDate(alert.getCreatedDate());
		return dto;
	}

	// Inner class for Kafka events
	public static class AlertEvent {
		private String eventType;
		private AlertDto data;
		private LocalDateTime timestamp;

		public AlertEvent(String eventType, AlertDto data) {
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

		public AlertDto getData() {
			return data;
		}

		public void setData(AlertDto data) {
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