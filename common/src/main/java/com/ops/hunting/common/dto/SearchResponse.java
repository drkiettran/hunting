package com.ops.hunting.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse<T> {

	private java.util.List<T> content;

	private Integer page;

	private Integer size;

	private Long totalElements;

	private Integer totalPages;

	private Boolean first;

	private Boolean last;

	private String sortBy;

	private String sortDirection;
}