package com.ops.hunting.common.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

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
class DtoValidationTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
		factory.afterPropertiesSet();
		validator = factory.getValidator();
	}

	@Test
	@DisplayName("Should validate UserRequest DTO")
	void shouldValidateUserRequestDTO() {
		UserRequest userRequest = UserRequest.builder().username("analyst1").email("analyst1@company.com")
				.firstName("John").lastName("Doe").role(UserRole.ANALYST).password("SecurePass123!").build();

		Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Should fail validation for invalid email format")
	void shouldFailValidationForInvalidEmail() {
		UserRequest userRequest = UserRequest.builder().username("analyst1").email("invalid-email") // Invalid email
																									// format
				.firstName("John").lastName("Doe").role(UserRole.ANALYST).password("SecurePass123!").build();

		Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
	}

	@Test
	@DisplayName("Should fail validation for weak password")
	void shouldFailValidationForWeakPassword() {
		UserRequest userRequest = UserRequest.builder().username("analyst1").email("analyst1@company.com")
				.firstName("John").lastName("Doe").role(UserRole.ANALYST).password("weak") // Too weak password
				.build();

		Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
	}

	@Test
	@DisplayName("Should validate ThreatIntelRequest DTO")
	void shouldValidateThreatIntelRequestDTO() {
		ThreatIntelRequest request = ThreatIntelRequest.builder().iocType("IP").iocValue("192.168.1.100")
				.threatLevel("HIGH").source("MISP").confidence(85).description("Malicious IP from botnet").build();

		Set<ConstraintViolation<ThreatIntelRequest>> violations = validator.validate(request);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Should fail validation for invalid confidence in ThreatIntelRequest")
	void shouldFailValidationForInvalidConfidence() {
		ThreatIntelRequest request = ThreatIntelRequest.builder().iocType("IP").iocValue("192.168.1.100")
				.threatLevel("HIGH").source("MISP").confidence(150) // Invalid confidence > 100
				.build();

		Set<ConstraintViolation<ThreatIntelRequest>> violations = validator.validate(request);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("confidence")));
	}

	@Test
	@DisplayName("Should validate AlertRequest DTO")
	void shouldValidateAlertRequestDTO() {
		AlertRequest request = AlertRequest.builder().title("Suspicious Network Activity")
				.description("Unusual outbound connections detected").severity("CRITICAL").ruleId("RULE_001")
				.sourceIp("10.0.0.15").destinationIp("192.168.1.100").protocol("TCP").port(443)
				.detectedAt(LocalDateTime.now()).build();

		Set<ConstraintViolation<AlertRequest>> violations = validator.validate(request);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Should validate InvestigationRequest DTO")
	void shouldValidateInvestigationRequestDTO() {
		InvestigationRequest request = InvestigationRequest.builder().title("Potential Data Exfiltration")
				.description("Investigation into suspicious data transfer patterns").priority("HIGH")
				.assigneeId(UUID.randomUUID()).alertIds(Set.of(UUID.randomUUID(), UUID.randomUUID()))
				.tags("data-exfiltration,network-analysis").build();

		Set<ConstraintViolation<InvestigationRequest>> violations = validator.validate(request);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Should validate LoginRequest DTO")
	void shouldValidateLoginRequestDTO() {
		LoginRequest request = LoginRequest.builder().username("analyst1").password("SecurePass123!").rememberMe(true)
				.build();

		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Should fail validation for missing required fields in LoginRequest")
	void shouldFailValidationForMissingLoginFields() {
		LoginRequest request = LoginRequest.builder().username("") // Empty username
				.password("") // Empty password
				.build();

		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
	}

	@Test
	@DisplayName("Should validate PasswordChangeRequest DTO")
	void shouldValidatePasswordChangeRequestDTO() {
		PasswordChangeRequest request = PasswordChangeRequest.builder().currentPassword("OldPass123!")
				.newPassword("NewSecurePass123!").confirmPassword("NewSecurePass123!").build();

		Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(request);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Should validate SearchRequest DTO")
	void shouldValidateSearchRequestDTO() {
		SearchRequest request = SearchRequest.builder().query("malicious IP").entityType("ThreatIntel").page(0).size(20)
				.sortBy("createdAt").sortDirection("DESC").startDate(LocalDateTime.now().minusDays(7))
				.endDate(LocalDateTime.now()).build();

		Set<ConstraintViolation<SearchRequest>> violations = validator.validate(request);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Should validate BulkOperationRequest DTO")
	void shouldValidateBulkOperationRequestDTO() {
		BulkOperationRequest request = BulkOperationRequest.builder().operation("DELETE")
				.entityIds(Set.of(UUID.randomUUID(), UUID.randomUUID())).reason("Cleaning up old data").build();

		Set<ConstraintViolation<BulkOperationRequest>> violations = validator.validate(request);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Should fail validation for empty entity IDs in BulkOperationRequest")
	void shouldFailValidationForEmptyEntityIds() {
		BulkOperationRequest request = BulkOperationRequest.builder().operation("DELETE").entityIds(Set.of()) // Empty
																												// set
				.reason("Cleaning up old data").build();

		Set<ConstraintViolation<BulkOperationRequest>> violations = validator.validate(request);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("entityIds")));
	}
}