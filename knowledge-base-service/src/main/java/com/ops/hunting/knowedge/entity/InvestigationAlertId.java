package com.ops.hunting.knowedge.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class InvestigationAlertId implements Serializable {

	@Column(name = "investigation_id")
	private String investigationId;

	@Column(name = "alert_id")
	private String alertId;

	// Constructors
	public InvestigationAlertId() {
	}

	public InvestigationAlertId(String investigationId, String alertId) {
		this.investigationId = investigationId;
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
	public String getInvestigationId() {
		return investigationId;
	}

	public void setInvestigationId(String investigationId) {
		this.investigationId = investigationId;
	}

	public String getAlertId() {
		return alertId;
	}

	public void setAlertId(String alertId) {
		this.alertId = alertId;
	}
}
