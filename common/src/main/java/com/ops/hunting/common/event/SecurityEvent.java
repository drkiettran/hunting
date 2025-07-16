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
 * Base security event
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityEvent {

	@Builder.Default
	private UUID eventId = UUID.randomUUID();

	private String eventType;

	private String userId;

	private String ipAddress;

	private String userAgent;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();

	private Boolean success;

	private String failureReason;

	private String resourceAccessed;

	private String actionPerformed;

	private Map<String, Object> additionalData;

	private String sessionId;

	private Integer riskScore;
}