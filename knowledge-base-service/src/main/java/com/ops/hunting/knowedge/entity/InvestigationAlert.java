package com.ops.hunting.knowedge.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "investigation_alerts")
public class InvestigationAlert {

	@EmbeddedId
	private InvestigationAlertId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("investigationId")
	@JoinColumn(name = "investigation_id")
	private Investigation investigation;

	@Column(name = "alert_id")
	private UUID alertId;

	@Column(name = "analysis_notes", columnDefinition = "TEXT")
	private String analysisNotes;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@PrePersist
	protected void onCreate() {
		createdDate = LocalDateTime.now();
	}

	// Constructors
	public InvestigationAlert() {
	}

	public InvestigationAlert(Investigation investigation, UUID alertId, String analysisNotes) {
		this.id = new InvestigationAlertId(investigation.getId(), alertId);
		this.investigation = investigation;
		this.alertId = alertId;
		this.analysisNotes = analysisNotes;
	}

	// Getters and setters
	public InvestigationAlertId getId() {
		return id;
	}

	public void setId(InvestigationAlertId id) {
		this.id = id;
	}

	public Investigation getInvestigation() {
		return investigation;
	}

	public void setInvestigation(Investigation investigation) {
		this.investigation = investigation;
	}

	public UUID getAlertId() {
		return alertId;
	}

	public void setAlertId(UUID alertId) {
		this.alertId = alertId;
	}

	public String getAnalysisNotes() {
		return analysisNotes;
	}

	public void setAnalysisNotes(String analysisNotes) {
		this.analysisNotes = analysisNotes;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
}
