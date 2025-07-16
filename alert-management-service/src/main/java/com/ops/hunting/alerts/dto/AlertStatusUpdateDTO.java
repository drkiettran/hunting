package com.ops.hunting.alerts.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertStatusUpdateDTO {
	private UUID alertId;
	private String previousStatus;
	private String newStatus;
	private String updatedBy;
	private LocalDateTime updatedAt;
	private String notes;
}