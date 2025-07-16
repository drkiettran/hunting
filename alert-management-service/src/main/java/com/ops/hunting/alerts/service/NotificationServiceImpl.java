package com.ops.hunting.alerts.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ops.hunting.alerts.dto.AlertDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void sendCriticalAlertNotification(AlertDTO alert) {
		log.info("Sending critical alert notification for alert: {}", alert.getId());

		// Send to notification service via Kafka
		kafkaTemplate.send("critical-alert-notification", alert);

		// In a real implementation, this might also:
		// - Send email notifications
		// - Send SMS alerts
		// - Send Slack notifications
		// - Update monitoring dashboards
	}

	@Override
	public void sendAssignmentNotification(AlertDTO alert, String assignee) {
		log.info("Sending assignment notification for alert: {} to {}", alert.getId(), assignee);

		// Send notification about assignment
		kafkaTemplate.send("alert-assignment-notification", alert);
	}

	@Override
	public void sendStatusUpdateNotification(AlertDTO alert, String previousStatus) {
		log.info("Sending status update notification for alert: {} from {} to {}", alert.getId(), previousStatus,
				alert.getStatus());

		// Send notification about status change
		kafkaTemplate.send("alert-status-notification", alert);
	}
}