package com.ops.hunting.alerts.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ops.hunting.alerts.dto.AlertDTO;
import com.ops.hunting.alerts.dto.AlertSummaryDTO;
import com.ops.hunting.alerts.entity.Alert;
import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.enums.AlertStatus;
import com.ops.hunting.alerts.exception.AlertNotFoundException;
import com.ops.hunting.alerts.mapper.AlertMapper;
import com.ops.hunting.alerts.repository.AlertRepository;
import com.ops.hunting.alerts.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertServiceImpl implements AlertService {

	private final AlertRepository alertRepository;
	private final AlertMapper alertMapper;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final NotificationService notificationService;

	@Override
	@Transactional
	public AlertDTO createAlert(AlertDTO alertDTO) {
		log.info("Creating new alert: {}", alertDTO.getTitle());

		Alert alert = alertMapper.toEntity(alertDTO);
		alert.setCreatedAt(LocalDateTime.now());
		alert.setStatus(AlertStatus.OPEN);

		Alert savedAlert = alertRepository.save(alert);
		AlertDTO result = alertMapper.toDTO(savedAlert);

		// Send Kafka message
		kafkaTemplate.send("alert-created", result);

		// Send notification for high severity alerts
		if (alertDTO.getSeverity() == AlertSeverity.CRITICAL) {
			notificationService.sendCriticalAlertNotification(result);
		}

		log.info("Alert created successfully with ID: {}", savedAlert.getId());
		return result;
	}

	@Override
	@Cacheable(value = "alerts", key = "#id")
	public AlertDTO getAlertById(UUID id) {
		log.debug("Fetching alert with ID: {}", id);

		Alert alert = alertRepository.findById(id)
				.orElseThrow(() -> new AlertNotFoundException("Alert not found with id: " + id));

		return alertMapper.toDTO(alert);
	}

	@Override
	public Page<AlertDTO> getAllAlerts(Pageable pageable) {
		log.debug("Fetching all alerts with pagination: {}", pageable);

		Page<Alert> alerts = alertRepository.findAll(pageable);
		return alerts.map(alertMapper::toDTO);
	}

	@Override
	@Transactional
	@CacheEvict(value = "alerts", key = "#id")
	public AlertDTO updateAlert(UUID id, AlertDTO alertDTO) {
		log.info("Updating alert with ID: {}", id);

		Alert existingAlert = alertRepository.findById(id)
				.orElseThrow(() -> new AlertNotFoundException("Alert not found with id: " + id));

		// Update fields
		existingAlert.setTitle(alertDTO.getTitle());
		existingAlert.setDescription(alertDTO.getDescription());
		existingAlert.setSeverity(alertDTO.getSeverity());
		existingAlert.setResolutionNotes(alertDTO.getResolutionNotes());
		existingAlert.setUpdatedAt(LocalDateTime.now());

		Alert updatedAlert = alertRepository.save(existingAlert);
		AlertDTO result = alertMapper.toDTO(updatedAlert);

		// Send Kafka message
		kafkaTemplate.send("alert-updated", result);

		return result;
	}

	@Override
	@Transactional
	@CacheEvict(value = "alerts", key = "#id")
	public AlertDTO updateAlertStatus(UUID id, AlertStatus status, String assignedTo) {
		log.info("Updating alert status for ID: {} to {}", id, status);

		Alert alert = alertRepository.findById(id)
				.orElseThrow(() -> new AlertNotFoundException("Alert not found with id: " + id));

		AlertStatus previousStatus = alert.getStatus();
		alert.setStatus(status);
		alert.setAssignedTo(assignedTo);
		alert.setUpdatedAt(LocalDateTime.now());

		if (status == AlertStatus.CLOSED) {
			alert.setClosedAt(LocalDateTime.now());
			alert.setClosedBy(assignedTo);
		}

		Alert updatedAlert = alertRepository.save(alert);
		AlertDTO result = alertMapper.toDTO(updatedAlert);

		// Send Kafka message
		kafkaTemplate.send("alert-status-updated", result);

		return result;
	}

	@Override
	@Transactional
	@CacheEvict(value = "alerts", key = "#id")
	public void deleteAlert(UUID id) {
		log.info("Deleting alert with ID: {}", id);

		Alert alert = alertRepository.findById(id)
				.orElseThrow(() -> new AlertNotFoundException("Alert not found with id: " + id));

		alertRepository.delete(alert);

		// Send Kafka message
		kafkaTemplate.send("alert-deleted", id.toString());
	}

	@Override
	public Page<AlertDTO> searchAlerts(AlertSeverity severity, AlertStatus status, LocalDateTime from, LocalDateTime to,
			Pageable pageable) {
		log.debug("Searching alerts with criteria - severity: {}, status: {}, from: {}, to: {}", severity, status, from,
				to);

		Page<Alert> alerts = alertRepository.findBySeverityAndStatusAndCreatedAtBetween(severity, status, from, to,
				pageable);

		return alerts.map(alertMapper::toDTO);
	}

	@Override
	public AlertSummaryDTO getAlertSummary() {
		log.debug("Generating alert summary");

		return AlertSummaryDTO.builder().totalAlerts(alertRepository.count())
				.openAlerts(alertRepository.countByStatus(AlertStatus.OPEN))
				.inProgressAlerts(alertRepository.countByStatus(AlertStatus.IN_PROGRESS))
				.closedAlerts(alertRepository.countByStatus(AlertStatus.CLOSED))
				.resolvedAlerts(alertRepository.countByStatus(AlertStatus.RESOLVED))
				.falsePositiveAlerts(alertRepository.countByStatus(AlertStatus.FALSE_POSITIVE))
				.lowSeverityAlerts(alertRepository.countBySeverity(AlertSeverity.LOW))
				.mediumSeverityAlerts(alertRepository.countBySeverity(AlertSeverity.MEDIUM))
				.highSeverityAlerts(alertRepository.countBySeverity(AlertSeverity.HIGH))
				.criticalSeverityAlerts(alertRepository.countBySeverity(AlertSeverity.CRITICAL))
				.averageResolutionTime(alertRepository.calculateAverageResolutionTimeInSeconds())
				.alertsCreatedToday(alertRepository.countByCreatedAtBetween(
						LocalDateTime.now().withHour(0).withMinute(0).withSecond(0), LocalDateTime.now()))
				.alertsClosedToday(alertRepository.countByCreatedAtBetween(
						LocalDateTime.now().withHour(0).withMinute(0).withSecond(0), LocalDateTime.now()))
				.build();
	}

	@Override
	public List<AlertDTO> getAlertsByAssignee(String assignee) {
		log.debug("Fetching alerts for assignee: {}", assignee);

		List<Alert> alerts = alertRepository.findByAssignedTo(assignee);
		return alerts.stream().map(alertMapper::toDTO).collect(Collectors.toList());
	}

	@Override
	public List<AlertDTO> getStaleAlerts(int hoursThreshold) {
		log.debug("Fetching stale alerts older than {} hours", hoursThreshold);

		LocalDateTime threshold = LocalDateTime.now().minusHours(hoursThreshold);
		List<Alert> staleAlerts = alertRepository.findStaleOpenAlerts(threshold);

		return staleAlerts.stream().map(alertMapper::toDTO).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void bulkUpdateStatus(List<UUID> alertIds, AlertStatus status, String updatedBy) {
		log.info("Bulk updating {} alerts to status: {}", alertIds.size(), status);

		for (UUID alertId : alertIds) {
			try {
				updateAlertStatus(alertId, status, updatedBy);
			} catch (AlertNotFoundException e) {
				log.warn("Alert not found during bulk update: {}", alertId);
			}
		}
	}

	@Override
	public List<String> getSourceSystems() {
		log.debug("Fetching distinct source systems");
		return alertRepository.findDistinctSourceSystems();
	}
}