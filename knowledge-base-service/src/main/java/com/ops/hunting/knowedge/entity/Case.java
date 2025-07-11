package com.ops.hunting.knowedge.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ops.hunting.common.entity.BaseEntity;
import com.ops.hunting.common.enums.CaseStatus;
import com.ops.hunting.common.enums.Priority;
import com.ops.hunting.common.enums.SeverityLevel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "cases")
public class Case extends BaseEntity {

	@NotBlank
	@Column(nullable = false, length = 200)
	private String title;

	@NotBlank
	@Column(columnDefinition = "TEXT", nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Priority priority;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CaseStatus status;

	@NotBlank
	@Column(name = "created_by", nullable = false)
	private String createdBy;

	@Column(name = "last_updated")
	private LocalDateTime lastUpdated;

	@Enumerated(EnumType.STRING)
	@Column(name = "risk_level")
	private SeverityLevel riskLevel;

	@Column(name = "assigned_to")
	private String assignedTo;

	@Column(name = "due_date")
	private LocalDateTime dueDate;

	@OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Investigation> investigations = new ArrayList<>();

	@OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Ticket> tickets = new ArrayList<>();

	@PreUpdate
	protected void onUpdate() {
		super.onUpdate();
		lastUpdated = LocalDateTime.now();
	}

	// Constructors
	public Case() {
	}

	public Case(String title, String description, Priority priority, String createdBy) {
		this.title = title;
		this.description = description;
		this.priority = priority;
		this.createdBy = createdBy;
		this.status = CaseStatus.OPEN;
		this.lastUpdated = LocalDateTime.now();
	}

	// Business methods
	public boolean isHighPriority() {
		return priority == Priority.HIGH || priority == Priority.CRITICAL;
	}

	public boolean isOverdue() {
		return dueDate != null && LocalDateTime.now().isAfter(dueDate) && !isClosed();
	}

	public boolean isClosed() {
		return status == CaseStatus.CLOSED;
	}

	public long getOpenInvestigationCount() {
		return investigations.stream().filter(inv -> !inv.isClosed()).count();
	}

	public long getOpenTicketCount() {
		return tickets.stream().filter(ticket -> !ticket.isClosed()).count();
	}

	public void assignTo(String analyst) {
		this.assignedTo = analyst;
		this.lastUpdated = LocalDateTime.now();
	}

	public void updateStatus(CaseStatus newStatus) {
		this.status = newStatus;
		this.lastUpdated = LocalDateTime.now();
	}

	// Getters and setters
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public CaseStatus getStatus() {
		return status;
	}

	public void setStatus(CaseStatus status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public SeverityLevel getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(SeverityLevel riskLevel) {
		this.riskLevel = riskLevel;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

	public List<Investigation> getInvestigations() {
		return investigations;
	}

	public void setInvestigations(List<Investigation> investigations) {
		this.investigations = investigations;
	}

	public List<Ticket> getTickets() {
		return tickets;
	}

	public void setTickets(List<Ticket> tickets) {
		this.tickets = tickets;
	}
}