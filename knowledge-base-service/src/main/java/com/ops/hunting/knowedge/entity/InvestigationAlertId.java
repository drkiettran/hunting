package com.ops.hunting.knowedge.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class InvestigationAlertId implements Serializable {

	@Column(name = "investigation_id")
	private UUID investigationId;

	@Column(name = "alert_id")
	private UUID alertId;

	// Constructors
	public InvestigationAlertId() {
	}

	public InvestigationAlertId(UUID uuid, UUID alertId) {
		this.investigationId = uuid;
		this.alertId = alertId;
	}

	// Equals and hashCode
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		InvestigationAlertId that = (InvestigationAlertId) o;
		return Objects.equals(investigationId, that.investigationId) && Objects.equals(alertId, that.alertId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(investigationId, alertId);
	}

	// Getters and setters
	public UUID getInvestigationId() {
		return investigationId;
	}

	public void setInvestigationId(UUID investigationId) {
		this.investigationId = investigationId;
	}

	public UUID getAlertId() {
		return alertId;
	}

	public void setAlertId(UUID alertId) {
		this.alertId = alertId;
	}
}
