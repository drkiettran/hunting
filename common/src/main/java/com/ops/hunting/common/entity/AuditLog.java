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
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class AuditLog extends BaseEntity {

	@NotBlank(message = "Action is required")
	@Column(name = "action", nullable = false)
	private String action;

	@Column(name = "entity_type")
	private String entityType;

	@Column(name = "entity_id")
	private UUID entityId;

	@Column(name = "user_id")
	private UUID userId;

	@Column(name = "username")
	private String username;

	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp;

	@Column(name = "old_values", columnDefinition = "TEXT")
	private String oldValues;

	@Column(name = "new_values", columnDefinition = "TEXT")
	private String newValues;

	@Column(name = "ip_address")
	private String ipAddress;

	@Column(name = "user_agent", length = 1000)
	private String userAgent;

	public boolean isModificationAction() {
		return "UPDATE".equals(action) || "DELETE".equals(action);
	}
}
