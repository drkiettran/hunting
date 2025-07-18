package com.ops.hunting.gateway.filter;

import java.time.Duration;
import java.util.Objects;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingGlobalFilter implements GlobalFilter, Ordered, GatewayFilter {

	private final ReactiveRedisTemplate<String, Object> redisTemplate;
	private static final int RATE_LIMIT = 100; // requests per minute
	private static final Duration WINDOW_SIZE = Duration.ofMinutes(1);

	public RateLimitingGlobalFilter() {
		super();
		this.redisTemplate = null;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String clientId = getClientId(exchange);
		String key = "rate_limit:" + clientId;

		return redisTemplate.opsForValue().increment(key).flatMap(count -> {
			if (count == 1) {
				return redisTemplate.expire(key, WINDOW_SIZE).then(Mono.just(count));
			}
			return Mono.just(count);
		}).flatMap(count -> {
			if (count > RATE_LIMIT) {
				log.warn("Rate limit exceeded for client: {}", clientId);
				exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
				return exchange.getResponse().setComplete();
			}
			return chain.filter(exchange);
		});
	}

	private String getClientId(ServerWebExchange exchange) {
		// Get client ID from IP address or authentication
		String clientIp = exchange.getRequest().getRemoteAddress() != null
				? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
				: "unknown";

		// You can also get from JWT token if available
		String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			// Extract user from token for user-specific rate limiting
			return "user_" + Objects.hashCode(authHeader);
		}

		return "ip_" + clientIp;
	}

	@Override
	public int getOrder() {
		return 0;
	}
}