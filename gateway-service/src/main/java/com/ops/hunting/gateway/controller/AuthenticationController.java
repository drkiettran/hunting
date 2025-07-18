package com.ops.hunting.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ops.hunting.gateway.dto.LoginRequest;
import com.ops.hunting.gateway.dto.LoginResponse;
import com.ops.hunting.gateway.dto.RefreshTokenRequest;
import com.ops.hunting.gateway.dto.TokenValidationResponse;
import com.ops.hunting.gateway.security.JwtTokenProvider;
import com.ops.hunting.gateway.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthenticationController {

	private final AuthenticationService authenticationService;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/login")
	@Operation(summary = "User login", description = "Authenticate user and return JWT token")
	public Mono<ResponseEntity<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
		log.info("Login attempt for user: {}", request.getUsername());
		return authenticationService.authenticate(request).map(ResponseEntity::ok)
				.doOnSuccess(response -> log.info("Login successful for user: {}", request.getUsername()))
				.doOnError(error -> log.error("Login failed for user: {}", request.getUsername(), error));
	}

	@PostMapping("/refresh")
	@Operation(summary = "Refresh token", description = "Refresh JWT token using refresh token")
	public Mono<ResponseEntity<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
		log.info("Token refresh attempt");
		return authenticationService.refreshToken(request.getRefreshToken()).map(ResponseEntity::ok)
				.doOnSuccess(response -> log.info("Token refreshed successfully"))
				.doOnError(error -> log.error("Token refresh failed", error));
	}

	@GetMapping("/validate")
	@Operation(summary = "Validate token", description = "Validate JWT token")
	public Mono<ResponseEntity<TokenValidationResponse>> validateToken(
			@RequestHeader("Authorization") String authHeader) {

		String token = authHeader.replace("Bearer ", "");
		boolean isValid = jwtTokenProvider.validateToken(token);

		if (isValid) {
			String username = jwtTokenProvider.getUsernameFromToken(token);
			return Mono.just(ResponseEntity.ok(new TokenValidationResponse(true, username)));
		}

		return Mono.just(ResponseEntity.ok(new TokenValidationResponse(false, null)));
	}

	@PostMapping("/logout")
	@Operation(summary = "User logout", description = "Logout user and invalidate token")
	public Mono<ResponseEntity<Void>> logout(@RequestHeader("Authorization") String authHeader) {
		String token = authHeader.replace("Bearer ", "");
		return authenticationService.logout(token).then(Mono.fromRunnable(() -> SecurityContextHolder.clearContext()))
				.then(Mono.just(ResponseEntity.ok().build()));
	}
}