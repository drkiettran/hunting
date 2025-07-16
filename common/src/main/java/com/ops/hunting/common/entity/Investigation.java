package com.ops.hunting.common.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "investigations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Investigation extends BaseEntity {

	@NotBlank(message = "Investigation title is required")
	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description", length = 2000)
	private String description;

	@NotBlank(message = "Priority is required")
	@Column(name = "priority", nullable = false)
	private String priority;

	@NotBlank(message = "Status is required")
	@Column(name = "status", nullable = false)
	@Builder.Default
	private String status = "OPEN";

	@Column(name = "assignee_id")
	private UUID assigneeId;

	@Column(name = "created_by", nullable = false)
	private UUID createdBy;

	@Column(name = "closed_at")
	private LocalDateTime closedAt;

	@Column(name = "closed_by")
	private UUID closedBy;

	@Column(name = "tags")
	private String tags;

	@Column(name = "findings", columnDefinition = "TEXT")
	private String findings;

	@Column(name = "recommendations", columnDefinition = "TEXT")
	private String recommendations;

	public boolean isOpen() {
		return "OPEN".equals(status);
	}

	public boolean isClosed() {
		return "CLOSED".equals(status);
	}

	public boolean isHighPriority() {
		return "HIGH".equals(priority) || "CRITICAL".equals(priority);
	}
}
