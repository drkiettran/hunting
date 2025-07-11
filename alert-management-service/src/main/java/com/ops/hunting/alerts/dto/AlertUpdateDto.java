package com.ops.hunting.alerts.dto;

import com.ops.hunting.common.enums.AlertStatus;
import com.ops.hunting.common.enums.SeverityLevel;

public class AlertUpdateDto {

	private SeverityLevel severity;
	private AlertStatus status;
	private String description;
	private String assignedTo;
	private String resolutionNotes;

	// Constructors
	public AlertUpdateDto() {
	}

	// Getters and setters
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

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getResolutionNotes() {
		return resolutionNotes;
	}

	public void setResolutionNotes(String resolutionNotes) {
		this.resolutionNotes = resolutionNotes;
	}
}
