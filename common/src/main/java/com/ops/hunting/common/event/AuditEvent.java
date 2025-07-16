package com.ops.hunting.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Audit event
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {

	@Builder.Default
	private UUID eventId = UUID.randomUUID();

	private String action;

	private String entityType;

	private UUID entityId;

	private UUID userId;

	private String username;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();

	private String oldValues;

	private String newValues;

	private String ipAddress;

	private String userAgent;

	private String sessionId;

	private Boolean success;

	private String errorMessage;
}