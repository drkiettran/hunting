package com.ops.hunting.alerts.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ops.hunting.common.entity.BaseEntity;
import com.ops.hunting.common.enums.AlertStatus;
import com.ops.hunting.common.enums.SeverityLevel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "alerts")
public class Alert extends BaseEntity {

	@NotNull
	@Column(nullable = false)
	private LocalDateTime timestamp;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SeverityLevel severity;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AlertStatus status;

	@NotBlank
	@Column(columnDefinition = "TEXT", nullable = false)
	private String description;

	@Column(name = "raw_data", columnDefinition = "TEXT")
	private String rawData;

	@Column(name = "assigned_to")
	private String assignedTo;

	@Column(name = "false_positive")
	private Boolean falsePositive = false;

	@Column(name = "analytic_id")
	private String analyticId;

	@Column(name = "resolved_date")
	private LocalDateTime resolvedDate;

	@Column(name = "resolution_notes", columnDefinition = "TEXT")
	private String resolutionNotes;

	@OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<AlertIndicator> indicators = new ArrayList<>();

	// Constructors
	public Alert() {
	}

	public Alert(LocalDateTime timestamp, SeverityLevel severity, String description) {
		this.timestamp = timestamp;
		this.severity = severity;
		this.description = description;
		this.status = AlertStatus.NEW;
		this.falsePositive = false;
	}

	// Business methods
	public boolean isHighPriority() {
		return severity == SeverityLevel.HIGH || severity == SeverityLevel.CRITICAL;
	}

	public boolean isAssigned() {
		return assignedTo != null && !assignedTo.trim().isEmpty();
	}

	public boolean isResolved() {
		return status == AlertStatus.RESOLVED || status == AlertStatus.FALSE_POSITIVE;
	}

	public void assignTo(String analyst) {
		this.assignedTo = analyst;
		if (this.status == AlertStatus.NEW) {
			this.status = AlertStatus.ASSIGNED;
		}
	}

	public void markAsInProgress() {
		this.status = AlertStatus.IN_PROGRESS;
	}

	public void resolve(String notes) {
		this.status = AlertStatus.RESOLVED;
		this.resolvedDate = LocalDateTime.now();
		this.resolutionNotes = notes;
	}

	public void markAsFalsePositive(String notes) {
		this.status = AlertStatus.FALSE_POSITIVE;
		this.falsePositive = true;
		this.resolvedDate = LocalDateTime.now();
		this.resolutionNotes = notes;
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

	public LocalDateTime getResolvedDate() {
		return resolvedDate;
	}

	public void setResolvedDate(LocalDateTime resolvedDate) {
		this.resolvedDate = resolvedDate;
	}

	public String getResolutionNotes() {
		return resolutionNotes;
	}

	public void setResolutionNotes(String resolutionNotes) {
		this.resolutionNotes = resolutionNotes;
	}

	public List<AlertIndicator> getIndicators() {
		return indicators;
	}

	public void setIndicators(List<AlertIndicator> indicators) {
		this.indicators = indicators;
	}
}
