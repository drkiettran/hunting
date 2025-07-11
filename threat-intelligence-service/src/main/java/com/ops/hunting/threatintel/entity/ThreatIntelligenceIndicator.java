package com.ops.hunting.threatintel.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

@Entity
@Table(name = "threat_intelligence_indicators")
public class ThreatIntelligenceIndicator {

	@EmbeddedId
	private ThreatIntelligenceIndicatorId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("threatIntelligenceId")
	@JoinColumn(name = "threat_intelligence_id")
	private ThreatIntelligence threatIntelligence;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("indicatorId")
	@JoinColumn(name = "indicator_id")
	private Indicator indicator;

	@DecimalMin(value = "0.0")
	@DecimalMax(value = "1.0")
	@Column(precision = 3, scale = 2)
	private BigDecimal confidence;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@PrePersist
	protected void onCreate() {
		createdDate = LocalDateTime.now();
	}

	// Constructors
	public ThreatIntelligenceIndicator() {
	}

	public ThreatIntelligenceIndicator(ThreatIntelligence threatIntelligence, Indicator indicator,
			BigDecimal confidence) {
		this.id = new ThreatIntelligenceIndicatorId(threatIntelligence.getId(), indicator.getId());
		this.threatIntelligence = threatIntelligence;
		this.indicator = indicator;
		this.confidence = confidence;
	}

	// Getters and setters
	public ThreatIntelligenceIndicatorId getId() {
		return id;
	}

	public void setId(ThreatIntelligenceIndicatorId id) {
		this.id = id;
	}

	public ThreatIntelligence getThreatIntelligence() {
		return threatIntelligence;
	}

	public void setThreatIntelligence(ThreatIntelligence threatIntelligence) {
		this.threatIntelligence = threatIntelligence;
	}

	public Indicator getIndicator() {
		return indicator;
	}

	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}

	public BigDecimal getConfidence() {
		return confidence;
	}

	public void setConfidence(BigDecimal confidence) {
		this.confidence = confidence;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
}