package com.ops.hunting.common.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {

	private Long totalAlerts;

	private Long openAlerts;

	private Long criticalAlerts;

	private Long activeInvestigations;

	private Long threatIntelItems;

	private Long activeThreatIntel;

	private Map<String, Long> alertsBySeverity;

	private Map<String, Long> alertsByStatus;

	private Map<String, Long> investigationsByPriority;

	private Map<String, Long> threatIntelByType;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime lastUpdated;
}