package com.ops.hunting.common.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.ops.hunting.common.dto.AlertDto;
import com.ops.hunting.common.dto.AlertRequest;

/**
 * Mock AlertService implementation for testing
 */
public class MockAlertService implements AlertService {

	private final Map<UUID, AlertDto> alerts = new HashMap<>();

	@Override
	public AlertDto createAlert(AlertRequest request) {
		AlertDto alert = AlertDto.builder().id(UUID.randomUUID()).title(request.getTitle())
				.description(request.getDescription()).severity(request.getSeverity()).status("OPEN")
				.ruleId(request.getRuleId()).sourceIp(request.getSourceIp()).destinationIp(request.getDestinationIp())
				.detectedAt(LocalDateTime.now()).createdAt(LocalDateTime.now()).build();

		alerts.put(alert.getId(), alert);
		return alert;
	}

	@Override
	public AlertDto updateAlert(UUID alertId, AlertRequest request) {
		// Mock implementation
		return alerts.get(alertId);
	}

	@Override
	public void acknowledgeAlert(UUID alertId, UUID userId) {
		// Mock implementation
	}

	@Override
	public void resolveAlert(UUID alertId, UUID userId, String resolution) {
		// Mock implementation
	}

	@Override
	public void markAsFalsePositive(UUID alertId, UUID userId, String reason) {
		// Mock implementation
	}

	@Override
	public long getOpenAlertsCount() {
		return alerts.values().stream().filter(alert -> "OPEN".equals(alert.getStatus())).count();
	}
}
