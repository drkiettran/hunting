package com.ops.hunting.common.service;

import java.util.UUID;

import com.ops.hunting.common.dto.AlertDto;
import com.ops.hunting.common.dto.AlertRequest;

public interface AlertService {

	/**
	 * Create new alert
	 */
	AlertDto createAlert(AlertRequest request);

	/**
	 * Update alert
	 */
	AlertDto updateAlert(UUID alertId, AlertRequest request);

	/**
	 * Acknowledge alert
	 */
	void acknowledgeAlert(UUID alertId, UUID userId);

	/**
	 * Resolve alert
	 */
	void resolveAlert(UUID alertId, UUID userId, String resolution);

	/**
	 * Mark alert as false positive
	 */
	void markAsFalsePositive(UUID alertId, UUID userId, String reason);

	/**
	 * Get open alerts count
	 */
	long getOpenAlertsCount();
}
