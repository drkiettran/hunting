package com.ops.hunting.common.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheUtils {

	private static final String CACHE_KEY_SEPARATOR = ":";
	private static final int DEFAULT_TTL_SECONDS = 3600; // 1 hour
	private static final int MIN_TTL_SECONDS = 60; // 1 minute
	private static final int MAX_TTL_SECONDS = 86400; // 24 hours

	private CacheUtils() {
		// Utility class
	}

	/**
	 * Generate cache key from components
	 */
	public static String generateKey(String... components) {
		if (components == null || components.length == 0) {
			throw new IllegalArgumentException("Cache key components cannot be empty");
		}

		return String.join(CACHE_KEY_SEPARATOR, Arrays.stream(components).filter(Objects::nonNull).map(String::trim)
				.filter(s -> !s.isEmpty()).toArray(String[]::new));
	}

	/**
	 * Generate cache key for threat intelligence
	 */
	public static String generateThreatIntelKey(String iocType, String iocValue) {
		return generateKey("threat_intel", iocType, iocValue);
	}

	/**
	 * Generate cache key for user
	 */
	public static String generateUserKey(String username) {
		return generateKey("user", username);
	}

	/**
	 * Generate cache key for alert
	 */
	public static String generateAlertKey(UUID alertId) {
		return generateKey("alert", alertId.toString());
	}

	/**
	 * Generate cache key for investigation
	 */
	public static String generateInvestigationKey(UUID investigationId) {
		return generateKey("investigation", investigationId.toString());
	}

	/**
	 * Validate TTL value
	 */
	public static boolean isValidTtl(int ttlSeconds) {
		return ttlSeconds >= MIN_TTL_SECONDS && ttlSeconds <= MAX_TTL_SECONDS;
	}

	/**
	 * Get default TTL
	 */
	public static int getDefaultTtl() {
		return DEFAULT_TTL_SECONDS;
	}

	/**
	 * Calculate TTL based on data type
	 */
	public static int calculateTtl(String dataType) {
		switch (dataType.toLowerCase()) {
		case "user":
			return 1800; // 30 minutes
		case "threat_intel":
			return 3600; // 1 hour
		case "alert":
			return 300; // 5 minutes
		case "investigation":
			return 600; // 10 minutes
		default:
			return DEFAULT_TTL_SECONDS;
		}
	}

	/**
	 * Sanitize cache key to ensure it's safe for Redis
	 */
	public static String sanitizeCacheKey(String key) {
		if (key == null) {
			return null;
		}

		// Remove any characters that might cause issues in Redis
		return key.replaceAll("[\\s\\n\\r\\t]", "_").replaceAll("[{}\\[\\]\"']", "").toLowerCase();
	}
}