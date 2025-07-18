package com.ops.hunting.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ops.hunting.gateway.dto.GatewayInfo;
import com.ops.hunting.gateway.dto.RouteInfo;
import com.ops.hunting.gateway.service.GatewayService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/gateway")
@RequiredArgsConstructor
@Tag(name = "Gateway", description = "Gateway management endpoints")
public class GatewayController {

	private final GatewayService gatewayService;

	@GetMapping("/info")
	@Operation(summary = "Get gateway info", description = "Get gateway service information")
	public Mono<GatewayInfo> getGatewayInfo() {
		return gatewayService.getGatewayInfo();
	}

	@GetMapping("/routes")
	@Operation(summary = "Get routes", description = "Get all configured routes")
	public Flux<RouteInfo> getRoutes() {
		return gatewayService.getRoutes();
	}
}
