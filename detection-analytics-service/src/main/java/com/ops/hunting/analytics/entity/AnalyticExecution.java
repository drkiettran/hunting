package com.ops.hunting.analytics.entity;

import com.ops.hunting.common.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytic_executions")
public class AnalyticExecution extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "analytic_id", nullable = false)
	private DetectionAnalytic analytic;

	@Column(name = "execution_time", nullable = false)
	private LocalDateTime executionTime;

	@Column(name = "execution_duration_ms")
	private Long executionDurationMs;

	@Column(name = "records_processed")
	private Long recordsProcessed;

	@Column(name = "alerts_generated")
	private Integer alertsGenerated = 0;

	@Column(name = "success")
	private Boolean success = true;

	@Column(name = "error_message", columnDefinition = "TEXT")
	private String errorMessage;

	@Column(name = "platform_response", columnDefinition = "TEXT")
	private String platformResponse;

	// Constructors
	public AnalyticExecution() {
	}

	public AnalyticExecution(DetectionAnalytic analytic, LocalDateTime executionTime) {
		this.analytic = analytic;
		this.executionTime = executionTime;
	}

	// Business methods
	public boolean isSuccessful() {
		return success != null && success;
	}

	public void markAsSuccessful(long durationMs, long recordsProcessed, int alertsGenerated) {
		this.success = true;
		this.executionDurationMs = durationMs;
		this.recordsProcessed = recordsProcessed;
		this.alertsGenerated = alertsGenerated;
		this.errorMessage = null;
	}

	public void markAsFailed(String errorMessage) {
		this.success = false;
		this.errorMessage = errorMessage;
	}

	// Getters and setters
	public DetectionAnalytic getAnalytic() {
		return analytic;
	}

	public void setAnalytic(DetectionAnalytic analytic) {
		this.analytic = analytic;
	}

	public LocalDateTime getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(LocalDateTime executionTime) {
		this.executionTime = executionTime;
	}

	public Long getExecutionDurationMs() {
		return executionDurationMs;
	}

	public void setExecutionDurationMs(Long executionDurationMs) {
		this.executionDurationMs = executionDurationMs;
	}

	public Long getRecordsProcessed() {
		return recordsProcessed;
	}

	public void setRecordsProcessed(Long recordsProcessed) {
		this.recordsProcessed = recordsProcessed;
	}

	public Integer getAlertsGenerated() {
		return alertsGenerated;
	}

	public void setAlertsGenerated(Integer alertsGenerated) {
		this.alertsGenerated = alertsGenerated;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getPlatformResponse() {
		return platformResponse;
	}

	public void setPlatformResponse(String platformResponse) {
		this.platformResponse = platformResponse;
	}
}
