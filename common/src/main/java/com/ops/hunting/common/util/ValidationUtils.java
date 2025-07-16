package com.ops.hunting.common.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ValidationUtils {

	private static final Set<String> VALID_THREAT_LEVELS = Set.of("LOW", "MEDIUM", "HIGH", "CRITICAL");
	private static final Set<String> VALID_IOC_TYPES = Set.of("IP", "DOMAIN", "URL", "FILE_HASH", "EMAIL",
			"REGISTRY_KEY");
	private static final Set<String> VALID_ROLES = Set.of("ADMIN", "ANALYST", "INVESTIGATOR", "VIEWER");
	private static final Set<String> VALID_ALERT_SEVERITIES = Set.of("LOW", "MEDIUM", "HIGH", "CRITICAL");
	private static final Set<String> VALID_ALERT_STATUSES = Set.of("OPEN", "ACKNOWLEDGED", "RESOLVED", "CLOSED",
			"FALSE_POSITIVE");
	private static final Set<String> VALID_INVESTIGATION_PRIORITIES = Set.of("LOW", "MEDIUM", "HIGH", "CRITICAL");
	private static final Set<String> VALID_INVESTIGATION_STATUSES = Set.of("OPEN", "IN_PROGRESS", "CLOSED", "ARCHIVED");

	private ValidationUtils() {
		// Utility class
	}

	/**
	 * Validate threat level
	 */
	public static boolean isValidThreatLevel(String threatLevel) {
		return threatLevel != null && VALID_THREAT_LEVELS.contains(threatLevel.toUpperCase());
	}

	/**
	 * Validate IOC type
	 */
	public static boolean isValidIocType(String iocType) {
		return iocType != null && VALID_IOC_TYPES.contains(iocType.toUpperCase());
	}

	/**
	 * Validate user role
	 */
	public static boolean isValidRole(String role) {
		return role != null && VALID_ROLES.contains(role.toUpperCase());
	}

	/**
	 * Validate alert severity
	 */
	public static boolean isValidAlertSeverity(String severity) {
		return severity != null && VALID_ALERT_SEVERITIES.contains(severity.toUpperCase());
	}

	/**
	 * Validate alert status
	 */
	public static boolean isValidAlertStatus(String status) {
		return status != null && VALID_ALERT_STATUSES.contains(status.toUpperCase());
	}

	/**
	 * Validate investigation priority
	 */
	public static boolean isValidInvestigationPriority(String priority) {
		return priority != null && VALID_INVESTIGATION_PRIORITIES.contains(priority.toUpperCase());
	}

	/**
	 * Validate investigation status
	 */
	public static boolean isValidInvestigationStatus(String status) {
		return status != null && VALID_INVESTIGATION_STATUSES.contains(status.toUpperCase());
	}

	/**
	 * Validate confidence score (0-100)
	 */
	public static boolean isValidConfidenceScore(Integer confidence) {
		return confidence != null && confidence >= 0 && confidence <= 100;
	}

	/**
	 * Validate UUID string format
	 */
	public static boolean isValidUuidString(String uuid) {
		if (uuid == null || uuid.trim().isEmpty()) {
			return false;
		}

		try {
			UUID.fromString(uuid);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Validate email format (simple validation)
	 */
	public static boolean isValidEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}

		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
		return email.matches(emailRegex);
	}

	/**
	 * Validate port number
	 */
	public static boolean isValidPort(Integer port) {
		return port != null && port >= 1 && port <= 65535;
	}

	/**
	 * Validate that string is not null or empty
	 */
	public static boolean isNotEmpty(String value) {
		return value != null && !value.trim().isEmpty();
	}

	/**
	 * Get all valid threat levels
	 */
	public static Set<String> getValidThreatLevels() {
		return new HashSet<>(VALID_THREAT_LEVELS);
	}

	/**
	 * Get all valid IOC types
	 */
	public static Set<String> getValidIocTypes() {
		return new HashSet<>(VALID_IOC_TYPES);
	}

	/**
	 * Get all valid roles
	 */
	public static Set<String> getValidRoles() {
		return new HashSet<>(VALID_ROLES);
	}
}
