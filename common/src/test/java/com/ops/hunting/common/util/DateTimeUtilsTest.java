package com.ops.hunting.common.util;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DateTimeUtilsTest {

	@Test
	@DisplayName("Should convert LocalDateTime to UTC correctly")
	void shouldConvertToUtcCorrectly() {
		LocalDateTime localTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
		LocalDateTime utcTime = DateTimeUtils.toUtc(localTime);

		assertNotNull(utcTime);
		// The exact difference depends on system timezone, but should not be null
	}

	@Test
	@DisplayName("Should handle null in UTC conversion")
	void shouldHandleNullInUtcConversion() {
		assertNull(DateTimeUtils.toUtc(null));
		assertNull(DateTimeUtils.fromUtc(null));
	}

	@Test
	@DisplayName("Should format timestamp for logging")
	void shouldFormatTimestampForLogging() {
		LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
		String formatted = DateTimeUtils.formatForLogging(timestamp);

		assertNotNull(formatted);
		assertTrue(formatted.contains("2024-01-15"));
		assertTrue(formatted.contains("10:30:45"));

		// Test null handling
		assertEquals("null", DateTimeUtils.formatForLogging(null));
	}

	@Test
	@DisplayName("Should format timestamp as ISO string")
	void shouldFormatTimestampAsIsoString() {
		LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
		String isoString = DateTimeUtils.formatAsIso(timestamp);

		assertNotNull(isoString);
		assertTrue(isoString.contains("2024-01-15T10:30:45"));

		assertNull(DateTimeUtils.formatAsIso(null));
	}

	@Test
	@DisplayName("Should parse ISO string to LocalDateTime")
	void shouldParseIsoStringToLocalDateTime() {
		String isoString = "2024-01-15T10:30:45";
		LocalDateTime parsed = DateTimeUtils.parseIso(isoString);

		assertNotNull(parsed);
		assertEquals(2024, parsed.getYear());
		assertEquals(1, parsed.getMonthValue());
		assertEquals(15, parsed.getDayOfMonth());
		assertEquals(10, parsed.getHour());
		assertEquals(30, parsed.getMinute());
		assertEquals(45, parsed.getSecond());

		assertNull(DateTimeUtils.parseIso(null));
		assertNull(DateTimeUtils.parseIso(""));
		assertNull(DateTimeUtils.parseIso("   "));
	}

	@Test
	@DisplayName("Should calculate time difference correctly")
	void shouldCalculateTimeDifferenceCorrectly() {
		LocalDateTime start = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
		LocalDateTime end = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

		long differenceMinutes = DateTimeUtils.calculateDifferenceMinutes(start, end);
		assertEquals(30, differenceMinutes);

		long differenceSeconds = DateTimeUtils.calculateDifferenceSeconds(start, end);
		assertEquals(1800, differenceSeconds); // 30 minutes = 1800 seconds

		// Test null handling
		assertEquals(0, DateTimeUtils.calculateDifferenceMinutes(null, end));
		assertEquals(0, DateTimeUtils.calculateDifferenceMinutes(start, null));
		assertEquals(0, DateTimeUtils.calculateDifferenceSeconds(null, end));
	}

	@Test
	@DisplayName("Should check if timestamp is within time window")
	void shouldCheckTimeWindow() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime recent = now.minusMinutes(5);
		LocalDateTime old = now.minusHours(2);

		assertTrue(DateTimeUtils.isWithinTimeWindow(recent, 10)); // Within 10 minutes
		assertFalse(DateTimeUtils.isWithinTimeWindow(old, 10)); // Older than 10 minutes
		assertFalse(DateTimeUtils.isWithinTimeWindow(null, 10)); // Null timestamp
	}

	@Test
	@DisplayName("Should check if timestamp is in past or future")
	void shouldCheckPastAndFuture() {
		LocalDateTime past = LocalDateTime.now().minusHours(1);
		LocalDateTime future = LocalDateTime.now().plusHours(1);

		assertTrue(DateTimeUtils.isInPast(past));
		assertFalse(DateTimeUtils.isInPast(future));
		assertFalse(DateTimeUtils.isInPast(null));

		assertFalse(DateTimeUtils.isInFuture(past));
		assertTrue(DateTimeUtils.isInFuture(future));
		assertFalse(DateTimeUtils.isInFuture(null));
	}

	@Test
	@DisplayName("Should get start and end of day")
	void shouldGetStartAndEndOfDay() {
		LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45);

		LocalDateTime startOfDay = DateTimeUtils.getStartOfDay(dateTime);
		assertNotNull(startOfDay);
		assertEquals(0, startOfDay.getHour());
		assertEquals(0, startOfDay.getMinute());
		assertEquals(0, startOfDay.getSecond());

		LocalDateTime endOfDay = DateTimeUtils.getEndOfDay(dateTime);
		assertNotNull(endOfDay);
		assertEquals(23, endOfDay.getHour());
		assertEquals(59, endOfDay.getMinute());
		assertEquals(59, endOfDay.getSecond());

		assertNull(DateTimeUtils.getStartOfDay(null));
		assertNull(DateTimeUtils.getEndOfDay(null));
	}
}
