package com.ops.hunting.alerts.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.enums.AlertStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String title;

	@Column(length = 1000)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AlertSeverity severity;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AlertStatus status;

	@Column(name = "source_system")
	private String sourceSystem;

	@Column(name = "source_ip")
	private String sourceIp;

	@Column(name = "destination_ip")
	private String destinationIp;

	@Column(name = "source_port")
	private Integer sourcePort;

	@Column(name = "destination_port")
	private Integer destinationPort;

	@Column(name = "protocol")
	private String protocol;

	@Column(name = "rule_id")
	private String ruleId;

	@Column(name = "rule_name")
	private String ruleName;

	@Column(name = "assigned_to")
	private String assignedTo;

	@Column(name = "closed_by")
	private String closedBy;

	@Column(name = "closed_at")
	private LocalDateTime closedAt;

	@Column(name = "resolution_notes", length = 2000)
	private String resolutionNotes;

	@Column(name = "tags")
	private String tags;

	@Column(name = "threat_category")
	private String threatCategory;

	@Column(name = "confidence_score")
	private Double confidenceScore;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "raw_data", length = 5000)
	private String rawData;

	@Column(name = "hash")
	private String hash;

	@Column(name = "investigation_id")
	private UUID investigationId;

	@Version
	private Long version;
}