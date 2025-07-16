package com.ops.hunting.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlingTest {

	@Test
	@DisplayName("Should create business exception with message and code")
	void shouldCreateBusinessExceptionWithMessageAndCode() {
		String message = "Threat intelligence source not found";
		String errorCode = "THREAT_INTEL_404";

		BusinessException exception = new BusinessException(message, errorCode);

		assertEquals(message, exception.getMessage());
		assertEquals(errorCode, exception.getErrorCode());
		assertNotNull(exception.getMetadata());
		assertTrue(exception.getMetadata().isEmpty());
	}

	@Test
	@DisplayName("Should create business exception with metadata")
	void shouldCreateBusinessExceptionWithMetadata() {
		String message = "Validation failed";
		String errorCode = "VALIDATION_ERROR";
		Map<String, Object> metadata = Map.of("field", "iocValue", "value", "invalid");

		BusinessException exception = new BusinessException(message, errorCode, metadata);

		assertEquals(message, exception.getMessage());
		assertEquals(errorCode, exception.getErrorCode());
		assertEquals(metadata.size(), exception.getMetadata().size());
		assertEquals("invalid", exception.getMetadata().get("value"));

		// Test adding metadata
		exception.addMetadata("newKey", "newValue");
		assertEquals("newValue", exception.getMetadata().get("newKey"));
	}

	@Test
	@DisplayName("Should create validation exception with field errors")
	void shouldCreateValidationExceptionWithFieldErrors() {
		ValidationException exception = new ValidationException();
		exception.addFieldError("email", "Invalid email format");
		exception.addFieldError("username", "Username already exists");

		assertTrue(exception.hasErrors());
		assertEquals(2, exception.getFieldErrors().size());
		assertTrue(exception.hasFieldError("email"));
		assertTrue(exception.hasFieldError("username"));
		assertEquals("Invalid email format", exception.getFieldError("email"));
		assertEquals("Username already exists", exception.getFieldError("username"));
		assertFalse(exception.hasFieldError("nonexistent"));
	}

	@Test
	@DisplayName("Should create validation exception from field errors map")
	void shouldCreateValidationExceptionFromFieldErrorsMap() {
		Map<String, String> fieldErrors = Map.of("email", "Invalid email format", "password", "Password too weak");

		ValidationException exception = new ValidationException(fieldErrors);

		assertTrue(exception.hasErrors());
		assertEquals(2, exception.getFieldErrors().size());
		assertTrue(exception.hasFieldError("email"));
		assertTrue(exception.hasFieldError("password"));
	}

	@Test
	@DisplayName("Should create security exception for access violations")
	void shouldCreateSecurityExceptionForAccessViolations() {
		String userId = "user123";
		String resource = "investigation_case_456";
		String action = "READ";
		String ipAddress = "192.168.1.100";

		SecurityException exception = new SecurityException("Access denied", userId, resource, action, ipAddress);

		assertEquals("Access denied", exception.getMessage());
		assertEquals("ACCESS_DENIED", exception.getErrorCode());
		assertEquals(userId, exception.getUserId());
		assertEquals(resource, exception.getResource());
		assertEquals(action, exception.getAction());
		assertEquals(ipAddress, exception.getIpAddress());
	}

	@Test
	@DisplayName("Should create entity not found exception")
	void shouldCreateEntityNotFoundException() {
		String entityType = "ThreatIntel";
		UUID entityId = UUID.randomUUID();

		EntityNotFoundException exception = new EntityNotFoundException(entityType, entityId);

		assertTrue(exception.getMessage().contains(entityType));
		assertTrue(exception.getMessage().contains(entityId.toString()));
		assertEquals("ENTITY_NOT_FOUND", exception.getErrorCode());
		assertEquals(entityType, exception.getEntityType());
		assertEquals(entityId, exception.getEntityId());
	}

	@Test
	@DisplayName("Should create authentication exception")
	void shouldCreateAuthenticationException() {
		String username = "testuser";
		String reason = "Invalid password";

		AuthenticationException exception = new AuthenticationException("Authentication failed", username, reason);

		assertEquals("Authentication failed", exception.getMessage());
		assertEquals(username, exception.getUsername());
		assertEquals(reason, exception.getReason());
	}

	@Test
	@DisplayName("Should create authorization exception")
	void shouldCreateAuthorizationException() {
		String userId = "user123";
		String resource = "admin_panel";
		String action = "ACCESS";
		String requiredRole = "ADMIN";
		String userRole = "ANALYST";

		AuthorizationException exception = new AuthorizationException("Insufficient privileges", userId, resource,
				action, requiredRole, userRole);

		assertEquals("Insufficient privileges", exception.getMessage());
		assertEquals(userId, exception.getUserId());
		assertEquals(resource, exception.getResource());
		assertEquals(action, exception.getAction());
		assertEquals(requiredRole, exception.getRequiredRole());
		assertEquals(userRole, exception.getUserRole());
	}
}
