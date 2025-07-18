package com.ops.hunting.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingGlobalFilter implements GlobalFilter, Ordered, GatewayFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		long startTime = System.currentTimeMillis();

		log.info("Request: {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());

		return chain.filter(exchange).doOnSuccess(aVoid -> {
			long endTime = System.currentTimeMillis();
			log.info("Response: {} - {} ms", exchange.getResponse().getStatusCode(), endTime - startTime);
		});
	}

	@Override
	public int getOrder() {
		return -1;
	}
}