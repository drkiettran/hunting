package com.ops.hunting.common.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

	private String query;

	private String entityType;

	@Builder.Default
	private Integer page = 0;

	@Builder.Default
	private Integer size = 20;

	private String sortBy;

	@Builder.Default
	private String sortDirection = "ASC";

	private Map<String, Object> filters;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime startDate;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime endDate;
}
