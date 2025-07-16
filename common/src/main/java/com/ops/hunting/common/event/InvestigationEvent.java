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
 * Investigation event
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestigationEvent {

	@Builder.Default
	private UUID eventId = UUID.randomUUID();

	private UUID investigationId;

	private String eventType; // CREATED, UPDATED, ASSIGNED, CLOSED, etc.

	private UUID userId;

	private String username;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();

	private String description;

	private Map<String, Object> changes;

	private String priority;

	private String status;
}