package com.ops.hunting.common.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.ops.hunting.common.enums.UserRole;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
class SecurityEntityTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
		factory.afterPropertiesSet();
		validator = factory.getValidator();
	}

	@Test
	@DisplayName("Should validate ThreatIntel entity correctly")
	void shouldValidateThreatIntelEntity() {
		ThreatIntel threatIntel = ThreatIntel.builder().iocType("IP").iocValue("192.168.1.100").threatLevel("HIGH")
				.source("MISP").confidence(85).description("Malicious IP from botnet").isActive(true).build();
		threatIntel.onCreate();

		Set<ConstraintViolation<ThreatIntel>> violations = validator.validate(threatIntel);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Should fail validation for invalid IOC value")
	void shouldFailValidationForInvalidIocValue() {
		ThreatIntel threatIntel = ThreatIntel.builder().iocType("IP").iocValue("") // Invalid empty value
				.threatLevel("HIGH").source("MISP").confidence(85).build();

		Set<ConstraintViolation<ThreatIntel>> violations = validator.validate(threatIntel);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("iocValue")));
	}

	@Test
	@DisplayName("Should fail validation for invalid confidence score")
	void shouldFailValidationForInvalidConfidence() {
		ThreatIntel threatIntel = ThreatIntel.builder().iocType("IP").iocValue("192.168.1.100").threatLevel("HIGH")
				.source("MISP").confidence(150) // Invalid confidence > 100
				.build();

		Set<ConstraintViolation<ThreatIntel>> violations = validator.validate(threatIntel);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("confidence")));
	}

	@Test
	@DisplayName("Should validate Alert entity with all required fields")
	void shouldValidateAlertEntity() {
		Alert alert = Alert.builder().title("Suspicious Network Activity")
				.description("Unusual outbound connections detected").severity("CRITICAL").status("OPEN")
				.ruleId("RULE_001").sourceIp("10.0.0.15").destinationIp("192.168.1.100").protocol("TCP").port(443)
				.detectedAt(LocalDateTime.now()).build();
		alert.onCreate();

		Set<ConstraintViolation<Alert>> violations = validator.validate(alert);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Should fail validation for missing required alert fields")
	void shouldFailValidationForMissingAlertFields() {
		Alert alert = Alert.builder().description("Missing title and other required fields").build();

		Set<ConstraintViolation<Alert>> violations = validator.validate(alert);
		assertFalse(violations.isEmpty());

		// Should have violations for title, severity, and ruleId
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("severity")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("ruleId")));
	}

	@Test
	@DisplayName("Should validate User entity correctly")
	void shouldValidateUserEntity() {
		User user = User.builder().username("analyst1").email("analyst1@company.com").passwordHash("hashedPassword123")
				.firstName("John").lastName("Doe").role(UserRole.ANALYST).isActive(true).build();
		user.onCreate();

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Should fail validation for invalid email in User entity")
	void shouldFailValidationForInvalidUserEmail() {
		User user = User.builder().username("analyst1").email("invalid-email") // Invalid email format
				.passwordHash("hashedPassword123").firstName("John").lastName("Doe").role(UserRole.ANALYST).build();

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
	}

	@Test
	@DisplayName("Should test ThreatIntel business methods")
	void shouldTestThreatIntelBusinessMethods() {
		ThreatIntel threatIntel = ThreatIntel.builder().confidence(90).expiresAt(LocalDateTime.now().minusDays(1)) // Expired
				.build();

		assertTrue(threatIntel.isHighConfidence());
		assertTrue(threatIntel.isExpired());
	}

	@Test
	@DisplayName("Should test Alert business methods")
	void shouldTestAlertBusinessMethods() {
		Alert alert = Alert.builder().status("OPEN").severity("CRITICAL").acknowledgedAt(LocalDateTime.now()).build();

		assertTrue(alert.isOpen());
		assertTrue(alert.isCritical());
		assertTrue(alert.isAcknowledged());
	}

	@Test
	@DisplayName("Should test User business methods")
	void shouldTestUserBusinessMethods() {
		User user = User.builder().firstName("John").lastName("Doe").lockedUntil(LocalDateTime.now().plusHours(1))
				.passwordResetToken("token123").passwordResetExpires(LocalDateTime.now().plusHours(1)).build();

		assertEquals("John Doe", user.getFullName());
		assertTrue(user.isLocked());
		assertTrue(user.isPasswordResetTokenValid());
	}
}