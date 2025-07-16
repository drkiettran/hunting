package com.ops.hunting.common.dto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOperationRequest {

	@NotBlank(message = "Operation type is required")
	private String operation;

	@NotEmpty(message = "Entity IDs are required")
	private Set<UUID> entityIds;

	private Map<String, Object> parameters;

	private String reason;
}
