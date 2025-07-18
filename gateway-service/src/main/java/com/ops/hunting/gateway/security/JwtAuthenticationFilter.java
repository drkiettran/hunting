package com.ops.hunting.gateway.security;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final ReactiveUserDetailsService userDetailsService;
	private final ReactiveRedisTemplate<String, Object> redisTemplate;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String token = resolveToken(exchange);

		if (token != null && jwtTokenProvider.validateToken(token)) {
			return isTokenBlacklisted(token).filter(blacklisted -> !blacklisted).flatMap(ignored -> authenticate(token))
					.flatMap(auth -> chain.filter(exchange)
							.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
					.switchIfEmpty(chain.filter(exchange));
		}

		return chain.filter(exchange);
	}

	private String resolveToken(ServerWebExchange exchange) {
		String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private Mono<Boolean> isTokenBlacklisted(String token) {
		return redisTemplate.hasKey("blacklist:" + token);
	}

	private Mono<UsernamePasswordAuthenticationToken> authenticate(String token) {
		String username = jwtTokenProvider.getUsernameFromToken(token);
		return userDetailsService.findByUsername(username)
				.map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities()));
	}
}