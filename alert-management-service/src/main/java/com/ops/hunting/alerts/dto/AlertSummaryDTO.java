package com.ops.hunting.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertSummaryDTO {
	private Long totalAlerts;
	private Long openAlerts;
	private Long inProgressAlerts;
	private Long closedAlerts;
	private Long resolvedAlerts;
	private Long falsePositiveAlerts;
	private Long lowSeverityAlerts;
	private Long mediumSeverityAlerts;
	private Long highSeverityAlerts;
	private Long criticalSeverityAlerts;
	private Double averageResolutionTime;
	private Long alertsCreatedToday;
	private Long alertsClosedToday;
}
