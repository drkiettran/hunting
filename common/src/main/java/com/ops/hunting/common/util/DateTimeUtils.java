package com.ops.hunting.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTimeUtils {

	private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private static final ZoneId UTC_ZONE = ZoneId.of("UTC");

	private DateTimeUtils() {
		// Utility class
	}

	/**
	 * Convert LocalDateTime to UTC
	 */
	public static LocalDateTime toUtc(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}

		ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
		return zonedDateTime.withZoneSameInstant(UTC_ZONE).toLocalDateTime();
	}

	/**
	 * Convert UTC to local time
	 */
	public static LocalDateTime fromUtc(LocalDateTime utcDateTime) {
		if (utcDateTime == null) {
			return null;
		}

		ZonedDateTime zonedDateTime = utcDateTime.atZone(UTC_ZONE);
		return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * Format timestamp for logging
	 */
	public static String formatForLogging(LocalDateTime timestamp) {
		if (timestamp == null) {
			return "null";
		}
		return timestamp.format(LOG_FORMATTER);
	}

	/**
	 * Format timestamp as ISO string
	 */
	public static String formatAsIso(LocalDateTime timestamp) {
		if (timestamp == null) {
			return null;
		}
		return timestamp.format(ISO_FORMATTER);
	}

	/**
	 * Parse ISO string to LocalDateTime
	 */
	public static LocalDateTime parseIso(String isoString) {
		if (isoString == null || isoString.trim().isEmpty()) {
			return null;
		}
		return LocalDateTime.parse(isoString, ISO_FORMATTER);
	}

	/**
	 * Calculate difference in minutes between two timestamps
	 */
	public static long calculateDifferenceMinutes(LocalDateTime start, LocalDateTime end) {
		if (start == null || end == null) {
			return 0;
		}
		return ChronoUnit.MINUTES.between(start, end);
	}

	/**
	 * Calculate difference in seconds between two timestamps
	 */
	public static long calculateDifferenceSeconds(LocalDateTime start, LocalDateTime end) {
		if (start == null || end == null) {
			return 0;
		}
		return ChronoUnit.SECONDS.between(start, end);
	}

	/**
	 * Check if timestamp is within specified time window (in minutes)
	 */
	public static boolean isWithinTimeWindow(LocalDateTime timestamp, int windowMinutes) {
		if (timestamp == null) {
			return false;
		}

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime windowStart = now.minusMinutes(windowMinutes);

		return timestamp.isAfter(windowStart) && timestamp.isBefore(now.plusMinutes(1));
	}

	/**
	 * Check if timestamp is in the past
	 */
	public static boolean isInPast(LocalDateTime timestamp) {
		if (timestamp == null) {
			return false;
		}
		return timestamp.isBefore(LocalDateTime.now());
	}

	/**
	 * Check if timestamp is in the future
	 */
	public static boolean isInFuture(LocalDateTime timestamp) {
		if (timestamp == null) {
			return false;
		}
		return timestamp.isAfter(LocalDateTime.now());
	}

	/**
	 * Get start of day for given date
	 */
	public static LocalDateTime getStartOfDay(LocalDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return dateTime.toLocalDate().atStartOfDay();
	}

	/**
	 * Get end of day for given date
	 */
	public static LocalDateTime getEndOfDay(LocalDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return dateTime.toLocalDate().atTime(23, 59, 59, 999999999);
	}
}
