package com.ops.hunting.alerts.entity;

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
@Table(name = "alert_indicators")
public class AlertIndicator {

	@EmbeddedId
	private AlertIndicatorId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("alertId")
	@JoinColumn(name = "alert_id")
	private Alert alert;

	@Column(name = "indicator_id")
	private String indicatorId;

	@DecimalMin(value = "0.0")
	@DecimalMax(value = "1.0")
	@Column(name = "relevance_score", precision = 3, scale = 2)
	private BigDecimal relevanceScore;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@PrePersist
	protected void onCreate() {
		createdDate = LocalDateTime.now();
	}

	// Constructors
	public AlertIndicator() {
	}

	public AlertIndicator(Alert alert, String indicatorId, BigDecimal relevanceScore) {
		this.id = new AlertIndicatorId(alert.getId(), indicatorId);
		this.alert = alert;
		this.indicatorId = indicatorId;
		this.relevanceScore = relevanceScore;
	}

	// Getters and setters
	public AlertIndicatorId getId() {
		return id;
	}

	public void setId(AlertIndicatorId id) {
		this.id = id;
	}

	public Alert getAlert() {
		return alert;
	}

	public void setAlert(Alert alert) {
		this.alert = alert;
	}

	public String getIndicatorId() {
		return indicatorId;
	}

	public void setIndicatorId(String indicatorId) {
		this.indicatorId = indicatorId;
	}

	public BigDecimal getRelevanceScore() {
		return relevanceScore;
	}

	public void setRelevanceScore(BigDecimal relevanceScore) {
		this.relevanceScore = relevanceScore;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
}
