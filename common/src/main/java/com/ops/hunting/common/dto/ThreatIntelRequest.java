package com.ops.hunting.common.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatIntelRequest {

	@NotBlank(message = "IOC type is required")
	private String iocType;

	@NotBlank(message = "IOC value is required")
	private String iocValue;

	@NotBlank(message = "Threat level is required")
	private String threatLevel;

	@NotBlank(message = "Source is required")
	private String source;

	@Min(value = 0, message = "Confidence must be between 0 and 100")
	@Max(value = 100, message = "Confidence must be between 0 and 100")
	private Integer confidence;

	private String description;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime expiresAt;

	private String tags;

	private String externalId;
}