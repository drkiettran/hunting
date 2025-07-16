package com.ops.hunting.common.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "investigation_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class InvestigationAlert extends BaseEntity {

	@Column(name = "investigation_id", nullable = false)
	private UUID investigationId;

	@Column(name = "alert_id", nullable = false)
	private UUID alertId;

	@Column(name = "added_by", nullable = false)
	private UUID addedBy;

	@Column(name = "added_at", nullable = false)
	private LocalDateTime addedAt;

	@Column(name = "notes")
	private String notes;

	@PrePersist
	protected void onCreateInvestigationAlert() {
		super.onCreate();
		if (addedAt == null) {
			addedAt = LocalDateTime.now();
		}
	}
}
