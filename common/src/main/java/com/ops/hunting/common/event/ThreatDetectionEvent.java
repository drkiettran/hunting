package com.ops.hunting.common.event;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Threat detection event
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatDetectionEvent {

	@Builder.Default
	private UUID eventId = UUID.randomUUID();

	private String ruleId;

	private String ruleName;

	private String severity;

	private String sourceIp;

	private String destinationIp;

	private String protocol;

	private Integer port;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Builder.Default
	private LocalDateTime detectionTime = LocalDateTime.now();

	private String rawData;

	private Map<String, Object> metadata;

	private String alertId;

	private Boolean isBlocked;

	private String blockReason;
}