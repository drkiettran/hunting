package com.ops.hunting.alerts.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ops.hunting.common.enums.SeverityLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AlertCreateDto {

	@NotNull(message = "Timestamp is required")
	private LocalDateTime timestamp;

	@NotNull(message = "Severity is required")
	private SeverityLevel severity;

	@NotBlank(message = "Description is required")
	private String description;

	private String rawData;
	private String analyticId;
	private List<String> indicatorIds;

	// Constructors
	public AlertCreateDto() {
	}

	public AlertCreateDto(LocalDateTime timestamp, SeverityLevel severity, String description) {
		this.timestamp = timestamp;
		this.severity = severity;
		this.description = description;
	}

	// Getters and setters
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public SeverityLevel getSeverity() {
		return severity;
	}

	public void setSeverity(SeverityLevel severity) {
		this.severity = severity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRawData() {
		return rawData;
	}

	public void setRawData(String rawData) {
		this.rawData = rawData;
	}

	public String getAnalyticId() {
		return analyticId;
	}

	public void setAnalyticId(String analyticId) {
		this.analyticId = analyticId;
	}

	public List<String> getIndicatorIds() {
		return indicatorIds;
	}

	public void setIndicatorIds(List<String> indicatorIds) {
		this.indicatorIds = indicatorIds;
	}
}
