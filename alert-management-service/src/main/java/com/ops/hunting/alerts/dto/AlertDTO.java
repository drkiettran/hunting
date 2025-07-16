package com.ops.hunting.alerts.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.enums.AlertStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDTO {

	private UUID id;

	@NotBlank(message = "Title is required")
	private String title;

	private String description;

	@NotNull(message = "Severity is required")
	private AlertSeverity severity;

	@NotNull(message = "Status is required")
	private AlertStatus status;

	private String sourceSystem;
	private String sourceIp;
	private String destinationIp;
	private Integer sourcePort;
	private Integer destinationPort;
	private String protocol;
	private String ruleId;
	private String ruleName;
	private String assignedTo;
	private String closedBy;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime closedAt;

	private String resolutionNotes;
	private String tags;
	private String threatCategory;
	private Double confidenceScore;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	private String rawData;
	private String hash;
	private UUID investigationId;
	private Long version;
}
