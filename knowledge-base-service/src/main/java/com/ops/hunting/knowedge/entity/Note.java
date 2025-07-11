package com.ops.hunting.knowedge.entity;

import com.ops.hunting.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "notes")
public class Note extends BaseEntity {

	@NotBlank
	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@NotBlank
	@Column(name = "created_by", nullable = false)
	private String createdBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id", nullable = false)
	private Ticket ticket;

	// Constructors
	public Note() {
	}

	public Note(String content, String createdBy, Ticket ticket) {
		this.content = content;
		this.createdBy = createdBy;
		this.ticket = ticket;
	}

	// Getters and setters
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}
}