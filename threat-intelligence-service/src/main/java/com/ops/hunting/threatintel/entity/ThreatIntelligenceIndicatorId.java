package com.ops.hunting.threatintel.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ThreatIntelligenceIndicatorId implements Serializable {

	@Column(name = "threat_intelligence_id")
	private UUID threatIntelligenceId;

	@Column(name = "indicator_id")
	private UUID indicatorId;

	// Constructors
	public ThreatIntelligenceIndicatorId() {
	}

	public ThreatIntelligenceIndicatorId(UUID threatIntelligenceId, UUID indicatorId) {
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
	public UUID getThreatIntelligenceId() {
		return threatIntelligenceId;
	}

	public void setThreatIntelligenceId(UUID threatIntelligenceId) {
		this.threatIntelligenceId = threatIntelligenceId;
	}

	public UUID getIndicatorId() {
		return indicatorId;
	}

	public void setIndicatorId(UUID indicatorId) {
		this.indicatorId = indicatorId;
	}
}
