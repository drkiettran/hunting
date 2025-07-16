package com.ops.hunting.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidationUtilsTest {

	@Test
	@DisplayName("Should validate threat level values")
	void shouldValidateThreatLevelValues() {
		assertTrue(ValidationUtils.isValidThreatLevel("LOW"));
		assertTrue(ValidationUtils.isValidThreatLevel("MEDIUM"));
		assertTrue(ValidationUtils.isValidThreatLevel("HIGH"));
		assertTrue(ValidationUtils.isValidThreatLevel("CRITICAL"));

		// Test case insensitive
		assertTrue(ValidationUtils.isValidThreatLevel("low"));
		assertTrue(ValidationUtils.isValidThreatLevel("High"));

		assertFalse(ValidationUtils.isValidThreatLevel("INVALID"));
		assertFalse(ValidationUtils.isValidThreatLevel(""));
		assertFalse(ValidationUtils.isValidThreatLevel(null));
	}

	@Test
	@DisplayName("Should validate IOC types")
	void shouldValidateIocTypes() {
		assertTrue(ValidationUtils.isValidIocType("IP"));
		assertTrue(ValidationUtils.isValidIocType("DOMAIN"));
		assertTrue(ValidationUtils.isValidIocType("URL"));
		assertTrue(ValidationUtils.isValidIocType("FILE_HASH"));
		assertTrue(ValidationUtils.isValidIocType("EMAIL"));
		assertTrue(ValidationUtils.isValidIocType("REGISTRY_KEY"));

		// Test case insensitive
		assertTrue(ValidationUtils.isValidIocType("ip"));
		assertTrue(ValidationUtils.isValidIocType("Domain"));

		assertFalse(ValidationUtils.isValidIocType("INVALID_TYPE"));
		assertFalse(ValidationUtils.isValidIocType(""));
		assertFalse(ValidationUtils.isValidIocType(null));
	}

	@Test
	@DisplayName("Should validate user roles")
	void shouldValidateUserRoles() {
		assertTrue(ValidationUtils.isValidRole("ADMIN"));
		assertTrue(ValidationUtils.isValidRole("ANALYST"));
		assertTrue(ValidationUtils.isValidRole("INVESTIGATOR"));
		assertTrue(ValidationUtils.isValidRole("VIEWER"));

		// Test case insensitive
		assertTrue(ValidationUtils.isValidRole("admin"));
		assertTrue(ValidationUtils.isValidRole("Analyst"));

		assertFalse(ValidationUtils.isValidRole("INVALID_ROLE"));
		assertFalse(ValidationUtils.isValidRole(""));
		assertFalse(ValidationUtils.isValidRole(null));
	}

	@Test
	@DisplayName("Should validate alert severities and statuses")
	void shouldValidateAlertSeveritiesAndStatuses() {
		// Severities
		assertTrue(ValidationUtils.isValidAlertSeverity("LOW"));
		assertTrue(ValidationUtils.isValidAlertSeverity("MEDIUM"));
		assertTrue(ValidationUtils.isValidAlertSeverity("HIGH"));
		assertTrue(ValidationUtils.isValidAlertSeverity("CRITICAL"));

		// Statuses
		assertTrue(ValidationUtils.isValidAlertStatus("OPEN"));
		assertTrue(ValidationUtils.isValidAlertStatus("ACKNOWLEDGED"));
		assertTrue(ValidationUtils.isValidAlertStatus("RESOLVED"));
		assertTrue(ValidationUtils.isValidAlertStatus("CLOSED"));
		assertTrue(ValidationUtils.isValidAlertStatus("FALSE_POSITIVE"));

		assertFalse(ValidationUtils.isValidAlertSeverity("INVALID"));
		assertFalse(ValidationUtils.isValidAlertStatus("INVALID"));
	}

	@Test
	@DisplayName("Should validate investigation priorities and statuses")
	void shouldValidateInvestigationPrioritiesAndStatuses() {
		// Priorities
		assertTrue(ValidationUtils.isValidInvestigationPriority("LOW"));
		assertTrue(ValidationUtils.isValidInvestigationPriority("MEDIUM"));
		assertTrue(ValidationUtils.isValidInvestigationPriority("HIGH"));
		assertTrue(ValidationUtils.isValidInvestigationPriority("CRITICAL"));

		// Statuses
		assertTrue(ValidationUtils.isValidInvestigationStatus("OPEN"));
		assertTrue(ValidationUtils.isValidInvestigationStatus("IN_PROGRESS"));
		assertTrue(ValidationUtils.isValidInvestigationStatus("CLOSED"));
		assertTrue(ValidationUtils.isValidInvestigationStatus("ARCHIVED"));

		assertFalse(ValidationUtils.isValidInvestigationPriority("INVALID"));
		assertFalse(ValidationUtils.isValidInvestigationStatus("INVALID"));
	}

	@Test
	@DisplayName("Should validate confidence scores")
	void shouldValidateConfidenceScores() {
		assertTrue(ValidationUtils.isValidConfidenceScore(0));
		assertTrue(ValidationUtils.isValidConfidenceScore(50));
		assertTrue(ValidationUtils.isValidConfidenceScore(100));

		assertFalse(ValidationUtils.isValidConfidenceScore(-1));
		assertFalse(ValidationUtils.isValidConfidenceScore(101));
		assertFalse(ValidationUtils.isValidConfidenceScore(null));
	}

	@Test
	@DisplayName("Should validate UUID strings")
	void shouldValidateUuidStrings() {
		String validUuid = UUID.randomUUID().toString();
		assertTrue(ValidationUtils.isValidUuidString(validUuid));

		assertFalse(ValidationUtils.isValidUuidString("invalid-uuid"));
		assertFalse(ValidationUtils.isValidUuidString(""));
		assertFalse(ValidationUtils.isValidUuidString(null));
		assertFalse(ValidationUtils.isValidUuidString("   "));
	}

	@Test
	@DisplayName("Should validate email addresses")
	void shouldValidateEmailAddresses() {
		assertTrue(ValidationUtils.isValidEmail("user@example.com"));
		assertTrue(ValidationUtils.isValidEmail("test.email+tag@example.org"));
		assertTrue(ValidationUtils.isValidEmail("user123@domain.co.uk"));

		assertFalse(ValidationUtils.isValidEmail("invalid-email"));
		assertFalse(ValidationUtils.isValidEmail("@example.com"));
		assertFalse(ValidationUtils.isValidEmail("user@"));
		assertFalse(ValidationUtils.isValidEmail(""));
		assertFalse(ValidationUtils.isValidEmail(null));
	}

	@Test
	@DisplayName("Should validate port numbers")
	void shouldValidatePortNumbers() {
		assertTrue(ValidationUtils.isValidPort(80));
		assertTrue(ValidationUtils.isValidPort(443));
		assertTrue(ValidationUtils.isValidPort(65535));
		assertTrue(ValidationUtils.isValidPort(1));

		assertFalse(ValidationUtils.isValidPort(0));
		assertFalse(ValidationUtils.isValidPort(-1));
		assertFalse(ValidationUtils.isValidPort(65536));
		assertFalse(ValidationUtils.isValidPort(null));
	}

	@Test
	@DisplayName("Should validate non-empty strings")
	void shouldValidateNonEmptyStrings() {
		assertTrue(ValidationUtils.isNotEmpty("test"));
		assertTrue(ValidationUtils.isNotEmpty("   test   ")); // Trimmed

		assertFalse(ValidationUtils.isNotEmpty(null));
		assertFalse(ValidationUtils.isNotEmpty(""));
		assertFalse(ValidationUtils.isNotEmpty("   "));
	}

	@Test
	@DisplayName("Should get valid enum values")
	void shouldGetValidEnumValues() {
		Set<String> threatLevels = ValidationUtils.getValidThreatLevels();
		assertNotNull(threatLevels);
		assertTrue(threatLevels.contains("LOW"));
		assertTrue(threatLevels.contains("HIGH"));
		assertEquals(4, threatLevels.size());

		Set<String> iocTypes = ValidationUtils.getValidIocTypes();
		assertNotNull(iocTypes);
		assertTrue(iocTypes.contains("IP"));
		assertTrue(iocTypes.contains("DOMAIN"));

		Set<String> roles = ValidationUtils.getValidRoles();
		assertNotNull(roles);
		assertTrue(roles.contains("ADMIN"));
		assertTrue(roles.contains("ANALYST"));
	}
}