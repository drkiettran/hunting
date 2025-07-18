package com.ops.hunting.gateway.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
		log.error("Authentication error: {}", ex.getMessage());

		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("error", "Unauthorized");
		errorResponse.put("message", "Authentication required");
		errorResponse.put("timestamp", System.currentTimeMillis());
		errorResponse.put("path", exchange.getRequest().getPath().value());

		try {
			String json = objectMapper.writeValueAsString(errorResponse);
			DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(json.getBytes());
			return exchange.getResponse().writeWith(Mono.just(buffer));
		} catch (JsonProcessingException e) {
			log.error("Error writing authentication error response", e);
			return exchange.getResponse().setComplete();
		}
	}
}