package com.ops.hunting.threatintel.entity;

import com.ops.hunting.common.entity.BaseEntity;
import com.ops.hunting.common.enums.IndicatorType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "indicators")
public class Indicator extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private IndicatorType type;

	@NotBlank
	@Column(nullable = false, length = 500)
	private String value;

	@Column(columnDefinition = "TEXT")
	private String description;

	@DecimalMin(value = "0.0", message = "Confidence must be between 0 and 1")
	@DecimalMax(value = "1.0", message = "Confidence must be between 0 and 1")
	@Column(precision = 3, scale = 2)
	private BigDecimal confidence;

	@OneToMany(mappedBy = "indicator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ThreatIntelligenceIndicator> threatIntelligenceIndicators = new ArrayList<>();

	// Constructors
	public Indicator() {
	}

	public Indicator(IndicatorType type, String value, String description, BigDecimal confidence) {
		this.type = type;
		this.value = value;
		this.description = description;
		this.confidence = confidence;
	}

	// Business methods
	public boolean isHighConfidence() {
		return confidence != null && confidence.compareTo(new BigDecimal("0.8")) >= 0;
	}

	// Getters and setters
	public IndicatorType getType() {
		return type;
	}

	public void setType(IndicatorType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getConfidence() {
		return confidence;
	}

	public void setConfidence(BigDecimal confidence) {
		this.confidence = confidence;
	}

	public List<ThreatIntelligenceIndicator> getThreatIntelligenceIndicators() {
		return threatIntelligenceIndicators;
	}

	public void setThreatIntelligenceIndicators(List<ThreatIntelligenceIndicator> threatIntelligenceIndicators) {
		this.threatIntelligenceIndicators = threatIntelligenceIndicators;
	}
}
