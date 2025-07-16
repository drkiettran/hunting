package com.ops.hunting.common.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class ConfigurationTest {

	@Test
	@DisplayName("Should load security configuration properties")
	void shouldLoadSecurityConfigurationProperties() {
		SecurityConfigurationProperties props = new SecurityConfigurationProperties();
		props.setJwtSecret("test-secret-key");
		props.setJwtExpirationMs(86400000L);
		props.setMaxLoginAttempts(5);
		props.setLockoutDurationMs(300000L);
		props.setPasswordResetExpiryMs(3600000L);
		props.setEnableTwoFactor(true);
		props.setEnableSessionTimeout(true);
		props.setSessionTimeoutMs(1800000L);

		assertEquals("test-secret-key", props.getJwtSecret());
		assertEquals(86400000L, props.getJwtExpirationMs());
		assertEquals(5, props.getMaxLoginAttempts());
		assertEquals(300000L, props.getLockoutDurationMs());
		assertEquals(3600000L, props.getPasswordResetExpiryMs());
		assertTrue(props.getEnableTwoFactor());
		assertTrue(props.getEnableSessionTimeout());
		assertEquals(1800000L, props.getSessionTimeoutMs());
	}

	@Test
	@DisplayName("Should validate configuration properties constraints")
	void shouldValidateConfigurationPropertiesConstraints() {
		SecurityConfigurationProperties props = new SecurityConfigurationProperties();

		// Test validation of positive values
		assertThrows(IllegalArgumentException.class, () -> props.setMaxLoginAttempts(0));

		assertThrows(IllegalArgumentException.class, () -> props.setMaxLoginAttempts(-1));

		assertThrows(IllegalArgumentException.class, () -> props.setJwtExpirationMs(-1L));

		assertThrows(IllegalArgumentException.class, () -> props.setJwtExpirationMs(0L));

		// Test valid values don't throw exceptions
		assertDoesNotThrow(() -> props.setMaxLoginAttempts(5));
		assertDoesNotThrow(() -> props.setJwtExpirationMs(86400000L));
	}

	@Test
	@DisplayName("Should load application configuration properties")
	void shouldLoadApplicationConfigurationProperties() {
		ApplicationConfigurationProperties props = new ApplicationConfigurationProperties();
		props.setName("Test Hunting Platform");
		props.setVersion("2.0.0");
		props.setEnvironment("test");
		props.setEnableMetrics(false);
		props.setEnableAuditLog(true);
		props.setEnableCaching(true);
		props.setDefaultCacheTtlSeconds(7200);
		props.setMaxFileUploadSize(104857600L); // 100MB
		props.setSupportEmail("support@test.com");

		assertEquals("Test Hunting Platform", props.getName());
		assertEquals("2.0.0", props.getVersion());
		assertEquals("test", props.getEnvironment());
		assertFalse(props.getEnableMetrics());
		assertTrue(props.getEnableAuditLog());
		assertTrue(props.getEnableCaching());
		assertEquals(7200, props.getDefaultCacheTtlSeconds());
		assertEquals(104857600L, props.getMaxFileUploadSize());
		assertEquals("support@test.com", props.getSupportEmail());
	}
}
