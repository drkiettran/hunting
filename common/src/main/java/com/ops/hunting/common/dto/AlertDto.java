package com.ops.hunting.common.dto;

import java.time.LocalDateTime;
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
public class AlertDto {

	private UUID id;

	@NotBlank(message = "Alert title is required")
	private String title;

	private String description;

	@NotBlank(message = "Severity is required")
	private String severity;

	@NotBlank(message = "Status is required")
	private String status;

	@NotBlank(message = "Rule ID is required")
	private String ruleId;

	private String ruleName;

	private String sourceIp;

	private String destinationIp;

	private String protocol;

	private Integer port;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime detectedAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime acknowledgedAt;

	private UUID acknowledgedBy;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime resolvedAt;

	private UUID resolvedBy;

	private UUID assignedTo;

	private String rawData;

	@Builder.Default
	private Boolean falsePositive = false;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime updatedAt;
}