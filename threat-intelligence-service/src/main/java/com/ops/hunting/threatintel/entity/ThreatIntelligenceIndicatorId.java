package com.ops.hunting.threatintel.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ThreatIntelligenceIndicatorId implements Serializable {

	@Column(name = "threat_intelligence_id")
	private String threatIntelligenceId;

	@Column(name = "indicator_id")
	private String indicatorId;

	// Constructors
	public ThreatIntelligenceIndicatorId() {
	}

	public ThreatIntelligenceIndicatorId(String threatIntelligenceId, String indicatorId) {
		this.threatIntelligenceId = threatIntelligenceId;
		this.indicatorId = indicatorId;
	}

	// Equals and hashCode
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ThreatIntelligenceIndicatorId that = (ThreatIntelligenceIndicatorId) o;
		return Objects.equals(threatIntelligenceId, that.threatIntelligenceId)
				&& Objects.equals(indicatorId, that.indicatorId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(threatIntelligenceId, indicatorId);
	}

	// Getters and setters
	public String getThreatIntelligenceId() {
		return threatIntelligenceId;
	}

	public void setThreatIntelligenceId(String threatIntelligenceId) {
		this.threatIntelligenceId = threatIntelligenceId;
	}

	public String getIndicatorId() {
		return indicatorId;
	}

	public void setIndicatorId(String indicatorId) {
		this.indicatorId = indicatorId;
	}
}
