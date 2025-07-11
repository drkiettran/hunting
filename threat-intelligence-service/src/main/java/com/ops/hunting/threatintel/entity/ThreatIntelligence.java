package com.ops.hunting.threatintel.entity;

import com.ops.hunting.common.entity.BaseEntity;
import com.ops.hunting.common.enums.SeverityLevel;
import com.ops.hunting.common.enums.ThreatType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "threat_intelligence")
public class ThreatIntelligence extends BaseEntity {

	@NotBlank
	@Column(nullable = false)
	private String source;

	@Enumerated(EnumType.STRING)
	@Column(name = "threat_type", nullable = false)
	private ThreatType threatType;

	@NotBlank
	@Column(columnDefinition = "TEXT", nullable = false)
	private String ttp;

	@NotBlank
	@Column(columnDefinition = "TEXT", nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SeverityLevel severity;

	@NotNull
	@Column(name = "discovered_date", nullable = false)
	private LocalDateTime discoveredDate;

	@NotBlank
	@Column(name = "reported_by", nullable = false)
	private String reportedBy;

	@OneToMany(mappedBy = "threatIntelligence", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ThreatIntelligenceIndicator> indicators = new ArrayList<>();

	// Constructors
	public ThreatIntelligence() {
	}

	public ThreatIntelligence(String source, ThreatType threatType, String ttp, String description,
			SeverityLevel severity, LocalDateTime discoveredDate, String reportedBy) {
		this.source = source;
		this.threatType = threatType;
		this.ttp = ttp;
		this.description = description;
		this.severity = severity;
		this.discoveredDate = discoveredDate;
		this.reportedBy = reportedBy;
	}

	// Business methods
	public boolean isHighPriority() {
		return severity == SeverityLevel.HIGH || severity == SeverityLevel.CRITICAL;
	}

	public boolean isRecent() {
		return discoveredDate.isAfter(LocalDateTime.now().minusDays(30));
	}

	// Getters and setters
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

	public List<ThreatIntelligenceIndicator> getIndicators() {
		return indicators;
	}

	public void setIndicators(List<ThreatIntelligenceIndicator> indicators) {
		this.indicators = indicators;
	}
}
