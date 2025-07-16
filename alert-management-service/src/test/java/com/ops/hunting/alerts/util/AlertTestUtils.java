package com.ops.hunting.alerts.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ops.hunting.alerts.dto.AlertDTO;
import com.ops.hunting.alerts.entity.Alert;
import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.enums.AlertStatus;

public class AlertTestUtils {

	public static AlertDTO createTestAlertDTO() {
		return AlertDTO.builder().id(UUID.randomUUID()).title("Test Alert").description("Test alert description")
				.severity(AlertSeverity.MEDIUM).status(AlertStatus.OPEN).sourceSystem("TEST_SYSTEM")
				.sourceIp("192.168.1.100").destinationIp("10.0.0.1").createdAt(LocalDateTime.now()).build();
	}

	public static Alert createTestAlert() {
		return Alert.builder().id(UUID.randomUUID()).title("Test Alert").description("Test alert description")
				.severity(AlertSeverity.MEDIUM).status(AlertStatus.OPEN).sourceSystem("TEST_SYSTEM")
				.sourceIp("192.168.1.100").destinationIp("10.0.0.1").createdAt(LocalDateTime.now()).build();
	}

	public static List<AlertDTO> createTestAlertList(int count) {
		List<AlertDTO> alerts = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			AlertDTO alert = createTestAlertDTO();
			alert.setTitle("Test Alert " + i);
			alerts.add(alert);
		}
		return alerts;
	}

	public static AlertDTO createHighSeverityAlert() {
		AlertDTO alert = createTestAlertDTO();
		alert.setSeverity(AlertSeverity.CRITICAL);
		alert.setTitle("Critical Security Incident");
		return alert;
	}

	public static AlertDTO createClosedAlert() {
		AlertDTO alert = createTestAlertDTO();
		alert.setStatus(AlertStatus.CLOSED);
		alert.setClosedAt(LocalDateTime.now());
		alert.setClosedBy("analyst1");
		return alert;
	}
}