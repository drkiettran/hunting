package com.ops.hunting.common.util;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

	@Test
	@DisplayName("Should hash password correctly")
	void shouldHashPasswordCorrectly() {
		String password = "testPassword123";
		String hashedPassword = SecurityUtils.hashPassword(password);

		assertNotNull(hashedPassword);
		assertNotEquals(password, hashedPassword);
		assertTrue(hashedPassword.length() > 50); // BCrypt hashes are typically 60 characters
	}

	@Test
	@DisplayName("Should verify password correctly")
	void shouldVerifyPasswordCorrectly() {
		String password = "testPassword123";
		String hashedPassword = SecurityUtils.hashPassword(password);

		assertTrue(SecurityUtils.verifyPassword(password, hashedPassword));
		assertFalse(SecurityUtils.verifyPassword("wrongPassword", hashedPassword));
	}

	@Test
	@DisplayName("Should throw exception for null password")
	void shouldThrowExceptionForNullPassword() {
		assertThrows(IllegalArgumentException.class, () -> SecurityUtils.hashPassword(null));
		assertThrows(IllegalArgumentException.class, () -> SecurityUtils.hashPassword(""));
		assertThrows(IllegalArgumentException.class, () -> SecurityUtils.hashPassword("   "));
	}

	@Test
	@DisplayName("Should handle null values in password verification")
	void shouldHandleNullValuesInPasswordVerification() {
		assertFalse(SecurityUtils.verifyPassword(null, "hash"));
		assertFalse(SecurityUtils.verifyPassword("password", null));
		assertFalse(SecurityUtils.verifyPassword(null, null));
	}

	@Test
	@DisplayName("Should generate secure random token")
	void shouldGenerateSecureRandomToken() {
		String token1 = SecurityUtils.generateToken();
		String token2 = SecurityUtils.generateToken();

		assertNotNull(token1);
		assertNotNull(token2);
		assertNotEquals(token1, token2);
		assertEquals(32, token1.length()); // Default length
	}

	@Test
	@DisplayName("Should generate token with specified length")
	void shouldGenerateTokenWithSpecifiedLength() {
		String token = SecurityUtils.generateToken(16);
		assertNotNull(token);
		assertEquals(16, token.length());
	}

	@Test
	@DisplayName("Should validate IP address format")
	void shouldValidateIpAddressFormat() {
		// Valid IPv4 addresses
		assertTrue(SecurityUtils.isValidIpAddress("192.168.1.1"));
		assertTrue(SecurityUtils.isValidIpAddress("10.0.0.1"));
		assertTrue(SecurityUtils.isValidIpAddress("127.0.0.1"));
		assertTrue(SecurityUtils.isValidIpAddress("8.8.8.8"));

		// Valid IPv6 addresses
		assertTrue(SecurityUtils.isValidIpAddress("2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
		assertTrue(SecurityUtils.isValidIpAddress("::1"));
		assertTrue(SecurityUtils.isValidIpAddress("::"));

		// Invalid addresses
		assertFalse(SecurityUtils.isValidIpAddress("256.1.1.1"));
		assertFalse(SecurityUtils.isValidIpAddress("192.168.1"));
		assertFalse(SecurityUtils.isValidIpAddress("invalid-ip"));
		assertFalse(SecurityUtils.isValidIpAddress(""));
		assertFalse(SecurityUtils.isValidIpAddress(null));
		assertFalse(SecurityUtils.isValidIpAddress("   "));
	}

	@Test
	@DisplayName("Should sanitize input correctly")
	void shouldSanitizeInputCorrectly() {
		String maliciousInput = "<script>alert('xss')</script>test";
		String sanitized = SecurityUtils.sanitizeInput(maliciousInput);

		assertFalse(sanitized.contains("<script>"));
		assertFalse(sanitized.contains("</script>"));
		assertTrue(sanitized.contains("test"));

		// Test HTML character escaping
		String htmlInput = "<div>Hello & \"World\"</div>";
		String htmlSanitized = SecurityUtils.sanitizeInput(htmlInput);
		assertTrue(htmlSanitized.contains("&lt;"));
		assertTrue(htmlSanitized.contains("&gt;"));
		assertTrue(htmlSanitized.contains("&amp;"));
		assertTrue(htmlSanitized.contains("&quot;"));
	}

	@Test
	@DisplayName("Should handle null input in sanitization")
	void shouldHandleNullInputInSanitization() {
		assertNull(SecurityUtils.sanitizeInput(null));
		assertEquals("", SecurityUtils.sanitizeInput(""));
	}

	@Test
	@DisplayName("Should validate password strength")
	void shouldValidatePasswordStrength() {
		// Strong passwords
		assertTrue(SecurityUtils.isStrongPassword("StrongPass123!"));
		assertTrue(SecurityUtils.isStrongPassword("Aa1@bcdefgh"));

		// Weak passwords
		assertFalse(SecurityUtils.isStrongPassword("password")); // No uppercase, numbers, special
		assertFalse(SecurityUtils.isStrongPassword("PASSWORD")); // No lowercase, numbers, special
		assertFalse(SecurityUtils.isStrongPassword("Password")); // No numbers, special
		assertFalse(SecurityUtils.isStrongPassword("Password1")); // No special characters
		assertFalse(SecurityUtils.isStrongPassword("Pass1!")); // Too short
		assertFalse(SecurityUtils.isStrongPassword(null)); // Null
		assertFalse(SecurityUtils.isStrongPassword("")); // Empty
	}

	@Test
	@DisplayName("Should generate secure session ID")
	void shouldGenerateSecureSessionId() {
		String sessionId1 = SecurityUtils.generateSessionId();
		String sessionId2 = SecurityUtils.generateSessionId();

		assertNotNull(sessionId1);
		assertNotNull(sessionId2);
		assertNotEquals(sessionId1, sessionId2);
		assertTrue(sessionId1.contains("-"));
		assertTrue(sessionId1.length() > 36); // UUID + timestamp
	}

	@Test
	@DisplayName("Should mask sensitive data for logging")
	void shouldMaskSensitiveDataForLogging() {
		assertEquals("****", SecurityUtils.maskSensitiveData(null));
		assertEquals("****", SecurityUtils.maskSensitiveData("abc"));
		assertEquals("****", SecurityUtils.maskSensitiveData(""));

		String longData = "sensitivepassword123";
		String masked = SecurityUtils.maskSensitiveData(longData);
		assertTrue(masked.contains("****"));
		assertTrue(masked.startsWith("se"));
		assertTrue(masked.endsWith("23"));
	}
}