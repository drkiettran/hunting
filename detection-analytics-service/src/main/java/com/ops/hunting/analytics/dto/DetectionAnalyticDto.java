package com.ops.hunting.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ops.hunting.common.enums.Platform;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DetectionAnalyticDto {

	private UUID id;

	@NotBlank(message = "Name is required")
	private String name;

	@NotBlank(message = "Description is required")
	private String description;

	@NotBlank(message = "Query text is required")
	private String queryText;

	@NotNull(message = "Platform is required")
	private Platform platform;

	@NotBlank(message = "Created by is required")
	private String createdBy;

	private LocalDateTime lastModified;
	private Boolean isActive;

	@DecimalMin(value = "0.0")
	@DecimalMax(value = "100.0")
	private BigDecimal accuracy;

	private String threatIntelligenceId;
	private Long executionCount;
	private LocalDateTime lastExecuted;
	private Long alertCount;
	private Long falsePositiveCount;
	private BigDecimal falsePositiveRate;
	private LocalDateTime createdDate;

	// Constructors
	public DetectionAnalyticDto() {
	}

	// Getters and setters
	public UUID getId() {
		return id;
	}

	public void setId(UUID uuid) {
		this.id = uuid;
	}

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

	public BigDecimal getFalsePositiveRate() {
		return falsePositiveRate;
	}

	public void setFalsePositiveRate(BigDecimal falsePositiveRate) {
		this.falsePositiveRate = falsePositiveRate;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
}