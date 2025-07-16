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
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Alert extends BaseEntity {

	@NotBlank(message = "Alert title is required")
	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description", length = 2000)
	private String description;

	@NotBlank(message = "Severity is required")
	@Column(name = "severity", nullable = false)
	private String severity;

	@NotBlank(message = "Status is required")
	@Column(name = "status", nullable = false)
	@Builder.Default
	private String status = "OPEN";

	@NotBlank(message = "Rule ID is required")
	@Column(name = "rule_id", nullable = false)
	private String ruleId;

	@Column(name = "rule_name")
	private String ruleName;

	@Column(name = "source_ip")
	private String sourceIp;

	@Column(name = "destination_ip")
	private String destinationIp;

	@Column(name = "protocol")
	private String protocol;

	@Column(name = "port")
	private Integer port;

	@Column(name = "detected_at")
	private LocalDateTime detectedAt;

	@Column(name = "acknowledged_at")
	private LocalDateTime acknowledgedAt;

	@Column(name = "acknowledged_by")
	private UUID acknowledgedBy;

	@Column(name = "resolved_at")
	private LocalDateTime resolvedAt;

	@Column(name = "resolved_by")
	private UUID resolvedBy;

	@Column(name = "assigned_to")
	private UUID assignedTo;

	@Column(name = "raw_data", columnDefinition = "TEXT")
	private String rawData;

	@Column(name = "false_positive")
	@Builder.Default
	private Boolean falsePositive = false;

	public boolean isOpen() {
		return "OPEN".equals(status);
	}

	public boolean isCritical() {
		return "CRITICAL".equals(severity);
	}

	public boolean isAcknowledged() {
		return acknowledgedAt != null;
	}
}
