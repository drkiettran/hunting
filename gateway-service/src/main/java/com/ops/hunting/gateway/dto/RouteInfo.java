package com.ops.hunting.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteInfo {
	private String name;
	private String path;
	private String uri;
}