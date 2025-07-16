
package com.ops.hunting.knowedge.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ops.hunting.common.entity.BaseEntity;
import com.ops.hunting.common.enums.AnalystTier;
import com.ops.hunting.common.enums.InvestigationStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "investigations")
public class Investigation extends BaseEntity {

	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;

	@NotBlank
	@Column(name = "assigned_analyst", nullable = false)
	private String assignedAnalyst;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AnalystTier tier;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private InvestigationStatus status;

	@Column(name = "end_date")
	private LocalDateTime endDate;

	@Column(columnDefinition = "TEXT")
	private String findings;

	@Column(columnDefinition = "TEXT")
	private String recommendations;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "case_id", nullable = false)
	private Case caseEntity;

	@OneToMany(mappedBy = "investigation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<InvestigationAlert> investigationAlerts = new ArrayList<>();

	@PrePersist
	public void onCreate() {
		super.onCreate();
		if (startDate == null) {
			startDate = LocalDateTime.now();
		}
		if (status == null) {
			status = InvestigationStatus.OPEN;
		}
	}

	// Constructors
	public Investigation() {
	}

	public Investigation(String assignedAnalyst, AnalystTier tier, Case caseEntity) {
		this.assignedAnalyst = assignedAnalyst;
		this.tier = tier;
		this.caseEntity = caseEntity;
		this.status = InvestigationStatus.OPEN;
		this.startDate = LocalDateTime.now();
	}

	// Business methods
	public boolean isClosed() {
		return status == InvestigationStatus.CLOSED;
	}

	public boolean isEscalated() {
		return status == InvestigationStatus.ESCALATED;
	}

	public boolean isInProgress() {
		return status == InvestigationStatus.IN_PROGRESS;
	}

	public void assignToTier(AnalystTier newTier, String newAnalyst) {
		this.tier = newTier;
		this.assignedAnalyst = newAnalyst;
		this.status = InvestigationStatus.ESCALATED;
	}

	public void markInProgress() {
		this.status = InvestigationStatus.IN_PROGRESS;
	}

	public void close(String findings, String recommendations) {
		this.status = InvestigationStatus.CLOSED;
		this.endDate = LocalDateTime.now();
		this.findings = findings;
		this.recommendations = recommendations;
	}

	public long getDurationInHours() {
		LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();
		return java.time.Duration.between(startDate, end).toHours();
	}

	// Getters and setters
	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public String getAssignedAnalyst() {
		return assignedAnalyst;
	}

	public void setAssignedAnalyst(String assignedAnalyst) {
		this.assignedAnalyst = assignedAnalyst;
	}

	public AnalystTier getTier() {
		return tier;
	}

	public void setTier(AnalystTier tier) {
		this.tier = tier;
	}

	public InvestigationStatus getStatus() {
		return status;
	}

	public void setStatus(InvestigationStatus status) {
		this.status = status;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public String getFindings() {
		return findings;
	}

	public void setFindings(String findings) {
		this.findings = findings;
	}

	public String getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(String recommendations) {
		this.recommendations = recommendations;
	}

	public Case getCaseEntity() {
		return caseEntity;
	}

	public void setCaseEntity(Case caseEntity) {
		this.caseEntity = caseEntity;
	}

	public List<InvestigationAlert> getInvestigationAlerts() {
		return investigationAlerts;
	}

	public void setInvestigationAlerts(List<InvestigationAlert> investigationAlerts) {
		this.investigationAlerts = investigationAlerts;
	}
}