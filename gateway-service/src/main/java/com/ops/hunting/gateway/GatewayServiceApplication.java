package com.ops.hunting.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				// User Management Service
				.route("user-service", r -> r.path("/api/users/**").uri("http://localhost:8081"))

				// Threat Intelligence Service
				.route("threat-intelligence-service",
						r -> r.path("/api/threat-intelligence/**").uri("http://localhost:8082"))

				// Detection Analytics Service
				.route("detection-analytics-service", r -> r.path("/api/analytics/**").uri("http://localhost:8083"))

				// Alert Management Service
				.route("alert-management-service", r -> r.path("/api/alerts/**").uri("http://localhost:8084"))

				// Investigation Service
				.route("investigation-service",
						r -> r.path("/api/investigations/**", "/api/cases/**", "/api/tickets/**")
								.uri("http://localhost:8085"))

				// Intelligence Product Service
				.route("intelligence-product-service", r -> r.path("/api/products/**").uri("http://localhost:8086"))

				// Knowledge Base Service
				.route("knowledge-base-service",
						r -> r.path("/api/knowledge/**", "/api/artifacts/**").uri("http://localhost:8087"))

				// Frontend Application
				.route("frontend", r -> r.path("/**").uri("http://localhost:3000"))

				.build();
	}
}
