package com.ops.hunting.common.util;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CacheUtilsTest {

	@Test
	@DisplayName("Should generate cache key correctly")
	void shouldGenerateCacheKeyCorrectly() {
		String key = CacheUtils.generateKey("threat_intel", "192.168.1.100", "IP");

		assertNotNull(key);
		assertTrue(key.contains("threat_intel"));
		assertTrue(key.contains("192.168.1.100"));
		assertTrue(key.contains("IP"));
		assertEquals("threat_intel:192.168.1.100:IP", key);
	}

	@Test
	@DisplayName("Should generate specific cache keys")
	void shouldGenerateSpecificCacheKeys() {
		// Test threat intel key
		String threatKey = CacheUtils.generateThreatIntelKey("IP", "192.168.1.100");
		assertEquals("threat_intel:IP:192.168.1.100", threatKey);

		// Test user key
		String userKey = CacheUtils.generateUserKey("analyst1");
		assertEquals("user:analyst1", userKey);

		// Test alert key
		UUID alertId = UUID.randomUUID();
		String alertKey = CacheUtils.generateAlertKey(alertId);
		assertEquals("alert:" + alertId.toString(), alertKey);

		// Test investigation key
		UUID investigationId = UUID.randomUUID();
		String investigationKey = CacheUtils.generateInvestigationKey(investigationId);
		assertEquals("investigation:" + investigationId.toString(), investigationKey);
	}

	@Test
	@DisplayName("Should handle null and empty components in key generation")
	void shouldHandleNullAndEmptyComponentsInKeyGeneration() {
		// Test with empty components
		assertThrows(IllegalArgumentException.class, () -> CacheUtils.generateKey());

		// Test with null components
		String key = CacheUtils.generateKey("threat_intel", null, "IP");
		assertEquals("threat_intel:IP", key);

		// Test with empty string components
		String keyWithEmpty = CacheUtils.generateKey("threat_intel", "", "IP");
		assertEquals("threat_intel:IP", keyWithEmpty);

		// Test with whitespace components
		String keyWithWhitespace = CacheUtils.generateKey("threat_intel", "   ", "IP");
		assertEquals("threat_intel:IP", keyWithWhitespace);
	}

	@Test
	@DisplayName("Should validate TTL settings")
	void shouldValidateTtlSettings() {
		assertTrue(CacheUtils.isValidTtl(3600)); // 1 hour
		assertTrue(CacheUtils.isValidTtl(86400)); // 1 day
		assertTrue(CacheUtils.isValidTtl(60)); // 1 minute (minimum)

		assertFalse(CacheUtils.isValidTtl(-1)); // Negative value
		assertFalse(CacheUtils.isValidTtl(0)); // Zero value
		assertFalse(CacheUtils.isValidTtl(59)); // Below minimum
		assertFalse(CacheUtils.isValidTtl(86401)); // Above maximum
	}

	@Test
	@DisplayName("Should provide default TTL")
	void shouldProvideDefaultTtl() {
		int defaultTtl = CacheUtils.getDefaultTtl();
		assertEquals(3600, defaultTtl); // 1 hour
	}

	@Test
	@DisplayName("Should calculate TTL based on data type")
	void shouldCalculateTtlBasedOnDataType() {
		assertEquals(1800, CacheUtils.calculateTtl("user")); // 30 minutes
		assertEquals(3600, CacheUtils.calculateTtl("threat_intel")); // 1 hour
		assertEquals(300, CacheUtils.calculateTtl("alert")); // 5 minutes
		assertEquals(600, CacheUtils.calculateTtl("investigation")); // 10 minutes
		assertEquals(3600, CacheUtils.calculateTtl("unknown")); // Default
		assertEquals(3600, CacheUtils.calculateTtl("THREAT_INTEL")); // Case insensitive
	}

	@Test
	@DisplayName("Should sanitize cache keys")
	void shouldSanitizeCacheKeys() {
		String key = "threat intel key with spaces";
		String sanitized = CacheUtils.sanitizeCacheKey(key);
		assertEquals("threat_intel_key_with_spaces", sanitized);

		String keyWithSpecialChars = "threat{intel}[key]\"with'special\"chars";
		String sanitizedSpecial = CacheUtils.sanitizeCacheKey(keyWithSpecialChars);
		assertEquals("threatintelkeywithspecialchars", sanitizedSpecial);

		String keyWithNewlines = "threat\nintel\rkey\twith\ncontrol\rchars";
		String sanitizedNewlines = CacheUtils.sanitizeCacheKey(keyWithNewlines);
		assertEquals("threat_intel_key_with_control_chars", sanitizedNewlines);

		assertNull(CacheUtils.sanitizeCacheKey(null));
	}
}