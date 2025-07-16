package com.ops.hunting.alerts.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ops.hunting.alerts.dto.AlertDTO;
import com.ops.hunting.alerts.dto.AlertSummaryDTO;
import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.enums.AlertStatus;

public interface AlertService {

	AlertDTO createAlert(AlertDTO alertDTO);

	AlertDTO getAlertById(UUID id);

	Page<AlertDTO> getAllAlerts(Pageable pageable);

	AlertDTO updateAlert(UUID id, AlertDTO alertDTO);

	AlertDTO updateAlertStatus(UUID id, AlertStatus status, String assignedTo);

	void deleteAlert(UUID id);

	Page<AlertDTO> searchAlerts(AlertSeverity severity, AlertStatus status, LocalDateTime from, LocalDateTime to,
			Pageable pageable);

	AlertSummaryDTO getAlertSummary();

	List<AlertDTO> getAlertsByAssignee(String assignee);

	List<AlertDTO> getStaleAlerts(int hoursThreshold);

	void bulkUpdateStatus(List<UUID> alertIds, AlertStatus status, String updatedBy);

	List<String> getSourceSystems();
}