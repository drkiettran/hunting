package com.ops.hunting.common.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestigationDto {

	private UUID id;

	@NotBlank(message = "Investigation title is required")
	private String title;

	private String description;

	@NotBlank(message = "Priority is required")
	private String priority;

	@NotBlank(message = "Status is required")
	private String status;

	private UUID assigneeId;

	private UUID createdBy;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime closedAt;

	private UUID closedBy;

	private String tags;

	private String findings;

	private String recommendations;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime updatedAt;

	private Set<UUID> alertIds;
}