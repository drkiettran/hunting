package com.ops.hunting.knowedge.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ops.hunting.common.entity.BaseEntity;
import com.ops.hunting.common.enums.Priority;
import com.ops.hunting.common.enums.TicketStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tickets")
public class Ticket extends BaseEntity {

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
	private TicketStatus status;

	@NotBlank
	@Column(name = "created_by", nullable = false)
	private String createdBy;

	@Column(name = "assigned_to")
	private String assignedTo;

	@Column(name = "due_date")
	private LocalDateTime dueDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "case_id", nullable = false)
	private Case caseEntity;

	@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Note> notes = new ArrayList<>();

	@Column(name = "resolution_notes", columnDefinition = "TEXT")
	private String resolutionNotes;

	@Column(name = "resolved_date")
	private LocalDateTime resolvedDate;

	// Constructors
	public Ticket() {
	}

	public Ticket(String title, String description, Priority priority, String createdBy, Case caseEntity) {
		this.title = title;
		this.description = description;
		this.priority = priority;
		this.createdBy = createdBy;
		this.caseEntity = caseEntity;
		this.status = TicketStatus.OPEN;
	}

	// Business methods
	public boolean isClosed() {
		return status == TicketStatus.CLOSED || status == TicketStatus.RESOLVED;
	}

	public boolean isOverdue() {
		return dueDate != null && LocalDateTime.now().isAfter(dueDate) && !isClosed();
	}

	public boolean isAssigned() {
		return assignedTo != null && !assignedTo.trim().isEmpty();
	}

	public void assignTo(String analyst) {
		this.assignedTo = analyst;
		if (this.status == TicketStatus.OPEN) {
			this.status = TicketStatus.ASSIGNED;
		}
	}

	public void markInProgress() {
		this.status = TicketStatus.IN_PROGRESS;
	}

	public void resolve(String resolutionNotes) {
		this.status = TicketStatus.RESOLVED;
		this.resolutionNotes = resolutionNotes;
		this.resolvedDate = LocalDateTime.now();
	}

	public void close() {
		this.status = TicketStatus.CLOSED;
		if (this.resolvedDate == null) {
			this.resolvedDate = LocalDateTime.now();
		}
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

	public TicketStatus getStatus() {
		return status;
	}

	public void setStatus(TicketStatus status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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

	public Case getCaseEntity() {
		return caseEntity;
	}

	public void setCaseEntity(Case caseEntity) {
		this.caseEntity = caseEntity;
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public String getResolutionNotes() {
		return resolutionNotes;
	}

	public void setResolutionNotes(String resolutionNotes) {
		this.resolutionNotes = resolutionNotes;
	}

	public LocalDateTime getResolvedDate() {
		return resolvedDate;
	}

	public void setResolvedDate(LocalDateTime resolvedDate) {
		this.resolvedDate = resolvedDate;
	}
}