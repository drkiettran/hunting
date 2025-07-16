package com.ops.hunting.common.util;

import java.util.Objects;
import java.util.Random;

public class StringUtils {

	private StringUtils() {
		// Utility class
	}

	/**
	 * Check if string is null or empty
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	/**
	 * Check if string is not null and not empty
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * Safe string comparison (null-safe)
	 */
	public static boolean equals(String str1, String str2) {
		return Objects.equals(str1, str2);
	}

	/**
	 * Safe string comparison ignoring case (null-safe)
	 */
	public static boolean equalsIgnoreCase(String str1, String str2) {
		if (str1 == null && str2 == null) {
			return true;
		}
		if (str1 == null || str2 == null) {
			return false;
		}
		return str1.equalsIgnoreCase(str2);
	}

	/**
	 * Truncate string to specified length
	 */
	public static String truncate(String str, int maxLength) {
		if (str == null) {
			return null;
		}
		if (str.length() <= maxLength) {
			return str;
		}
		return str.substring(0, maxLength - 3) + "...";
	}

	/**
	 * Convert camelCase to snake_case
	 */
	public static String camelToSnake(String camelCase) {
		if (isEmpty(camelCase)) {
			return camelCase;
		}

		return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
	}

	/**
	 * Convert snake_case to camelCase
	 */
	public static String snakeToCamel(String snakeCase) {
		if (isEmpty(snakeCase)) {
			return snakeCase;
		}

		StringBuilder result = new StringBuilder();
		boolean capitalizeNext = false;

		for (char c : snakeCase.toCharArray()) {
			if (c == '_') {
				capitalizeNext = true;
			} else {
				if (capitalizeNext) {
					result.append(Character.toUpperCase(c));
					capitalizeNext = false;
				} else {
					result.append(c);
				}
			}
		}

		return result.toString();
	}

	/**
	 * Generate a random string of specified length
	 */
	public static String generateRandomString(int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder result = new StringBuilder();
		Random random = new Random();

		for (int i = 0; i < length; i++) {
			result.append(chars.charAt(random.nextInt(chars.length())));
		}

		return result.toString();
	}
}