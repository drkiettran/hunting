package com.ops.hunting.alerts.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AlertIndicatorId implements Serializable {

	@Column(name = "alert_id")
	private String alertId;

	@Column(name = "indicator_id")
	private String indicatorId;

	// Constructors
	public AlertIndicatorId() {
	}

	public AlertIndicatorId(String alertId, String indicatorId) {
		this.alertId = alertId;
		this.indicatorId = indicatorId;
	}

	// Equals and hashCode
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		AlertIndicatorId that = (AlertIndicatorId) o;
		return Objects.equals(alertId, that.alertId) && Objects.equals(indicatorId, that.indicatorId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(alertId, indicatorId);
	}

	// Getters and setters
	public String getAlertId() {
		return alertId;
	}

	public void setAlertId(String alertId) {
		this.alertId = alertId;
	}

	public String getIndicatorId() {
		return indicatorId;
	}

	public void setIndicatorId(String indicatorId) {
		this.indicatorId = indicatorId;
	}
}
