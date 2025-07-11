package com.ops.hunting.gateway.filter;

import com.ops.hunting.gateway.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter implements WebFilter {

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return chain.filter(exchange);
		}

		String token = authHeader.substring(7);

		return jwtService.validateToken(token).flatMap(isValid -> {
			if (isValid) {
				String username = jwtService.extractUsername(token);
				List<String> roles = jwtService.extractRoles(token);

				List<SimpleGrantedAuthority> authorities = roles.stream()
						.map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());

				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
						authorities);

				return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
			} else {
				return chain.filter(exchange);
			}
		});
	}
}
