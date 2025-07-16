package com.ops.hunting.threatintel.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ops.hunting.common.enums.SeverityLevel;
import com.ops.hunting.common.enums.ThreatType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ThreatIntelligenceDto {

	private UUID id;

	@NotBlank(message = "Source is required")
	private String source;

	@NotNull(message = "Threat type is required")
	private ThreatType threatType;

	@NotBlank(message = "TTP is required")
	private String ttp;

	@NotBlank(message = "Description is required")
	private String description;

	@NotNull(message = "Severity is required")
	private SeverityLevel severity;

	@NotNull(message = "Discovered date is required")
	private LocalDateTime discoveredDate;

	@NotBlank(message = "Reported by is required")
	private String reportedBy;

	private List<IndicatorDto> indicators;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;

	// Constructors
	public ThreatIntelligenceDto() {
	}

	// Getters and setters
	public UUID getId() {
		return id;
	}

	public void setId(UUID uuid) {
		this.id = uuid;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public ThreatType getThreatType() {
		return threatType;
	}

	public void setThreatType(ThreatType threatType) {
		this.threatType = threatType;
	}

	public String getTtp() {
		return ttp;
	}

	public void setTtp(String ttp) {
		this.ttp = ttp;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SeverityLevel getSeverity() {
		return severity;
	}

	public void setSeverity(SeverityLevel severity) {
		this.severity = severity;
	}

	public LocalDateTime getDiscoveredDate() {
		return discoveredDate;
	}

	public void setDiscoveredDate(LocalDateTime discoveredDate) {
		this.discoveredDate = discoveredDate;
	}

	public String getReportedBy() {
		return reportedBy;
	}

	public void setReportedBy(String reportedBy) {
		this.reportedBy = reportedBy;
	}

	public List<IndicatorDto> getIndicators() {
		return indicators;
	}

	public void setIndicators(List<IndicatorDto> indicators) {
		this.indicators = indicators;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}
}
