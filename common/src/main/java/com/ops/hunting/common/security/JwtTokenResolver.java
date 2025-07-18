package com.ops.hunting.common.security;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

public class JwtTokenResolver {

	private static final String BEARER_PREFIX = "Bearer ";

	private JwtTokenResolver() {
		// Utility class
	}

	/**
	 * Resolve JWT token from HTTP request
	 */
	public static String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
		return extractTokenFromBearer(bearerToken);
	}

	/**
	 * Resolve JWT token from Authorization header value
	 */
	public static String resolveToken(String authorizationHeader) {
		return extractTokenFromBearer(authorizationHeader);
	}

	/**
	 * Extract token from Bearer header
	 */
	private static String extractTokenFromBearer(String bearerToken) {
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(BEARER_PREFIX.length());
		}
		return null;
	}

	/**
	 * Create Authorization header value with Bearer prefix
	 */
	public static String createAuthorizationHeader(String token) {
		return BEARER_PREFIX + token;
	}

	/**
	 * Check if authorization header has Bearer prefix
	 */
	public static boolean isBearerToken(String authorizationHeader) {
		return StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX);
	}
}