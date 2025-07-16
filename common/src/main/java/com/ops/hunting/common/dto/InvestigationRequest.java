package com.ops.hunting.common.dto;

import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestigationRequest {

	@NotBlank(message = "Investigation title is required")
	private String title;

	private String description;

	@NotBlank(message = "Priority is required")
	private String priority;

	private UUID assigneeId;

	private String tags;

	private Set<UUID> alertIds;
}
