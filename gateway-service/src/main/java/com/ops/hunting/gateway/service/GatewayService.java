package com.ops.hunting.gateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.stereotype.Service;

import com.ops.hunting.gateway.dto.GatewayInfo;
import com.ops.hunting.gateway.dto.RouteInfo;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GatewayService {

	private final RouteLocator routeLocator;

	@Value("${spring.application.name:gateway-service}")
	private String applicationName;

	@Value("${application.version:1.0.0}")
	private String applicationVersion;

	public Mono<GatewayInfo> getGatewayInfo() {
		return Mono.just(new GatewayInfo(applicationName, applicationVersion, "UP"));
	}

	public Flux<RouteInfo> getRoutes() {
		return routeLocator.getRoutes()
				.map(route -> new RouteInfo(route.getId(), route.getPredicate().toString(), route.getUri().toString()));
	}
}