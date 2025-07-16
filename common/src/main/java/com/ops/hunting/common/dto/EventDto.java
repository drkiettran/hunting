package com.ops.hunting.common.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

	private UUID id;

	@NotBlank(message = "Event type is required")
	private String eventType;

	private String userId;

	private String ipAddress;

	private String userAgent;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime timestamp;

	private Boolean success;

	private String failureReason;

	private String resourceAccessed;

	private String actionPerformed;

	private Map<String, Object> additionalData;

	private String sessionId;

	private Integer riskScore;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdAt;
}
