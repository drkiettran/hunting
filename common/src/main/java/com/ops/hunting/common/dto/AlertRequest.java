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
public class AlertRequest {

	@NotBlank(message = "Alert title is required")
	private String title;

	private String description;

	@NotBlank(message = "Severity is required")
	private String severity;

	@NotBlank(message = "Rule ID is required")
	private String ruleId;

	private String ruleName;

	private String sourceIp;

	private String destinationIp;

	private String protocol;

	private Integer port;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime detectedAt;

	private UUID assignedTo;

	private String rawData;
}
