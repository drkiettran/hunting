package com.ops.hunting.alerts.service;

import com.ops.hunting.alerts.dto.AlertDTO;

public interface NotificationService {
	void sendCriticalAlertNotification(AlertDTO alert);

	void sendAssignmentNotification(AlertDTO alert, String assignee);

	void sendStatusUpdateNotification(AlertDTO alert, String previousStatus);
}
