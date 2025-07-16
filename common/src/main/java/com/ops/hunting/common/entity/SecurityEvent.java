package com.ops.hunting.common.entity;

import java.time.LocalDateTime;

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
@Table(name = "security_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class SecurityEvent extends BaseEntity {

	@NotBlank(message = "Event type is required")
	@Column(name = "event_type", nullable = false)
	private String eventType;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "ip_address")
	private String ipAddress;

	@Column(name = "user_agent", length = 1000)
	private String userAgent;

	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp;

	@Column(name = "success")
	private Boolean success;

	@Column(name = "failure_reason")
	private String failureReason;

	@Column(name = "resource_accessed")
	private String resourceAccessed;

	@Column(name = "action_performed")
	private String actionPerformed;

	@Column(name = "additional_data", columnDefinition = "TEXT")
	private String additionalData;

	@Column(name = "session_id")
	private String sessionId;

	@Column(name = "risk_score")
	private Integer riskScore;

	public boolean isSuccessful() {
		return Boolean.TRUE.equals(success);
	}

	public boolean isHighRisk() {
		return riskScore != null && riskScore >= 80;
	}
}
