package com.ops.hunting.gateway.service;

import java.time.Duration;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Service;

import com.ops.hunting.gateway.dto.LoginRequest;
import com.ops.hunting.gateway.dto.LoginResponse;
import com.ops.hunting.gateway.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

	private final ReactiveAuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final ReactiveUserDetailsService userDetailsService;
	private final ReactiveRedisTemplate<String, Object> redisTemplate;

	public Mono<LoginResponse> authenticate(LoginRequest request) {
		return authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()))
				.cast(UsernamePasswordAuthenticationToken.class).map(auth -> {
					String token = jwtTokenProvider.generateToken(auth.getName());
					String refreshToken = jwtTokenProvider.generateRefreshToken(auth.getName());

					// Store refresh token in Redis
					redisTemplate.opsForValue()
							.set("refresh_token:" + auth.getName(), refreshToken, Duration.ofDays(30)).subscribe();

					return new LoginResponse(token, refreshToken, auth.getName(), jwtTokenProvider.getExpirationTime());
				}).doOnError(error -> {
					log.error("Authentication failed for user: {}", request.getUsername(), error);
					throw new BadCredentialsException("Invalid credentials");
				});
	}

	public Mono<LoginResponse> refreshToken(String refreshToken) {
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			return Mono.error(new BadCredentialsException("Invalid refresh token"));
		}

		String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

		return redisTemplate.opsForValue().get("refresh_token:" + username).cast(String.class)
				.filter(storedToken -> storedToken.equals(refreshToken))
				.switchIfEmpty(Mono.error(new BadCredentialsException("Refresh token not found")))
				.flatMap(token -> userDetailsService.findByUsername(username)).map(userDetails -> {
					String newToken = jwtTokenProvider.generateToken(username);
					return new LoginResponse(newToken, refreshToken, username, jwtTokenProvider.getExpirationTime());
				});
	}

	public Mono<Void> logout(String token) {
		String username = jwtTokenProvider.getUsernameFromToken(token);

		// Add token to blacklist
		return redisTemplate.opsForValue()
				.set("blacklist:" + token, "true", Duration.ofMillis(jwtTokenProvider.getExpirationTime()))
				.then(redisTemplate.delete("refresh_token:" + username)).then();
	}
}