package com.ops.hunting.gateway.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.ops.hunting.gateway.filter.LoggingGlobalFilter;
import com.ops.hunting.gateway.filter.RateLimitingGlobalFilter;

@Configuration
public class GatewayConfiguration {

	@Value("${gateway.cors.allowed-origins:*}")
	private String allowedOrigins;

	@Value("${gateway.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
	private String allowedMethods;

	@Value("${gateway.cors.allowed-headers:*}")
	private String allowedHeaders;

	@Value("${gateway.cors.allow-credentials:true}")
	private boolean allowCredentials;

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowedOriginPatterns(Collections.singletonList(allowedOrigins));
		corsConfig.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
		corsConfig.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
		corsConfig.setAllowCredentials(allowCredentials);
		corsConfig.setMaxAge(8000L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);

		return new CorsWebFilter(source);
	}

	@Bean
	public GlobalFilter customGlobalFilter() {
		return (exchange, chain) -> {
			exchange.getRequest().mutate().header("X-Gateway-Timestamp", String.valueOf(System.currentTimeMillis()))
					.build();
			return chain.filter(exchange);
		};
	}

	private final LoggingGlobalFilter loggingGlobalFilter = new LoggingGlobalFilter();
	private final RateLimitingGlobalFilter rateLimitingGlobalFilter = new RateLimitingGlobalFilter();

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("user-service", r -> r.path("/api/users/**")
						.filters(f -> f.stripPrefix(1).filter(loggingGlobalFilter).filter(rateLimitingGlobalFilter))
						.uri("lb://user-management-service"))

				.route("threat-intelligence", r -> r.path("/api/threats/**")
						.filters(f -> f.stripPrefix(1).filter(loggingGlobalFilter).filter(rateLimitingGlobalFilter))
						.uri("lb://threat-intelligence-service"))

				.route("detection-analytics", r -> r.path("/api/analytics/**")
						.filters(f -> f.stripPrefix(1).filter(loggingGlobalFilter).filter(rateLimitingGlobalFilter))
						.uri("lb://detection-analytics-service"))

				.route("alert-management", r -> r.path("/api/alerts/**")
						.filters(f -> f.stripPrefix(1).filter(loggingGlobalFilter).filter(rateLimitingGlobalFilter))
						.uri("lb://alert-management-service"))

				.route("investigation-service", r -> r.path("/api/investigations/**")
						.filters(f -> f.stripPrefix(1).filter(loggingGlobalFilter).filter(rateLimitingGlobalFilter))
						.uri("lb://investigation-service"))

				.build();
	}
}
