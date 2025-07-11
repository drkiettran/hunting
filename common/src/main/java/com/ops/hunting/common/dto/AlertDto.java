package com.ops.hunting.common.dto;

import com.ops.hunting.common.enums.AlertStatus;
import com.ops.hunting.common.enums.SeverityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AlertDto {
	private String id;

	@NotNull
	private LocalDateTime timestamp;

	@NotNull
	private SeverityLevel severity;

	@NotNull
	private AlertStatus status;

	@NotBlank
	private String description;

	private String rawData;
	private String assignedTo;
	private Boolean falsePositive;
	private String analyticId;
	private LocalDateTime createdDate;

	// Constructors
	public AlertDto() {
	}

	public AlertDto(LocalDateTime timestamp, SeverityLevel severity, String description) {
		this.timestamp = timestamp;
		this.severity = severity;
		this.description = description;
		this.status = AlertStatus.NEW;
		this.falsePositive = false;
	}

	// Getters and setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public AlertStatus getStatus() {
		return status;
	}

	public void setStatus(AlertStatus status) {
		this.status = status;
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

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public Boolean getFalsePositive() {
		return falsePositive;
	}

	public void setFalsePositive(Boolean falsePositive) {
		this.falsePositive = falsePositive;
	}

	public String getAnalyticId() {
		return analyticId;
	}

	public void setAnalyticId(String analyticId) {
		this.analyticId = analyticId;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
}
