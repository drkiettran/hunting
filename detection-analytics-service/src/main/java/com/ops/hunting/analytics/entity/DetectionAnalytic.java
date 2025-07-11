package com.ops.hunting.analytics.entity;

import com.ops.hunting.common.entity.BaseEntity;
import com.ops.hunting.common.enums.Platform;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "detection_analytics")
public class DetectionAnalytic extends BaseEntity {

	@NotBlank
	@Column(nullable = false, length = 200)
	private String name;

	@NotBlank
	@Column(columnDefinition = "TEXT", nullable = false)
	private String description;

	@NotBlank
	@Column(name = "query_text", columnDefinition = "TEXT", nullable = false)
	private String queryText;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Platform platform;

	@NotBlank
	@Column(name = "created_by", nullable = false)
	private String createdBy;

	@Column(name = "last_modified")
	private LocalDateTime lastModified;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@DecimalMin(value = "0.0")
	@DecimalMax(value = "100.0")
	@Column(precision = 5, scale = 2)
	private BigDecimal accuracy;

	@Column(name = "threat_intelligence_id")
	private String threatIntelligenceId;

	@Column(name = "execution_count")
	private Long executionCount = 0L;

	@Column(name = "last_executed")
	private LocalDateTime lastExecuted;

	@Column(name = "alert_count")
	private Long alertCount = 0L;

	@Column(name = "false_positive_count")
	private Long falsePositiveCount = 0L;

	@OneToMany(mappedBy = "analytic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<AnalyticExecution> executions = new ArrayList<>();

	// Constructors
	public DetectionAnalytic() {
	}

	public DetectionAnalytic(String name, String description, String queryText, Platform platform, String createdBy) {
		this.name = name;
		this.description = description;
		this.queryText = queryText;
		this.platform = platform;
		this.createdBy = createdBy;
		this.lastModified = LocalDateTime.now();
	}

	// Business methods
	public boolean isHighAccuracy() {
		return accuracy != null && accuracy.compareTo(new BigDecimal("80.0")) >= 0;
	}

	public BigDecimal getFalsePositiveRate() {
		if (alertCount == null || alertCount == 0) {
			return BigDecimal.ZERO;
		}
		long fpCount = falsePositiveCount != null ? falsePositiveCount : 0;
		return new BigDecimal(fpCount).divide(new BigDecimal(alertCount), 4, BigDecimal.ROUND_HALF_UP);
	}

	public void incrementExecutionCount() {
		this.executionCount = (this.executionCount != null ? this.executionCount : 0) + 1;
		this.lastExecuted = LocalDateTime.now();
	}

	public void incrementAlertCount() {
		this.alertCount = (this.alertCount != null ? this.alertCount : 0) + 1;
	}

	public void incrementFalsePositiveCount() {
		this.falsePositiveCount = (this.falsePositiveCount != null ? this.falsePositiveCount : 0) + 1;
	}

	// Getters and setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQueryText() {
		return queryText;
	}

	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public BigDecimal getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(BigDecimal accuracy) {
		this.accuracy = accuracy;
	}

	public String getThreatIntelligenceId() {
		return threatIntelligenceId;
	}

	public void setThreatIntelligenceId(String threatIntelligenceId) {
		this.threatIntelligenceId = threatIntelligenceId;
	}

	public Long getExecutionCount() {
		return executionCount;
	}

	public void setExecutionCount(Long executionCount) {
		this.executionCount = executionCount;
	}

	public LocalDateTime getLastExecuted() {
		return lastExecuted;
	}

	public void setLastExecuted(LocalDateTime lastExecuted) {
		this.lastExecuted = lastExecuted;
	}

	public Long getAlertCount() {
		return alertCount;
	}

	public void setAlertCount(Long alertCount) {
		this.alertCount = alertCount;
	}

	public Long getFalsePositiveCount() {
		return falsePositiveCount;
	}

	public void setFalsePositiveCount(Long falsePositiveCount) {
		this.falsePositiveCount = falsePositiveCount;
	}

	public List<AnalyticExecution> getExecutions() {
		return executions;
	}

	public void setExecutions(List<AnalyticExecution> executions) {
		this.executions = executions;
	}
}
