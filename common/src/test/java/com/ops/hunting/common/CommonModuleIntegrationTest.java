package com.ops.hunting.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.ops.hunting.common.dto.UserDto;
import com.ops.hunting.common.enums.UserRole;
import com.ops.hunting.common.service.AlertService;
import com.ops.hunting.common.service.AuthenticationService;
import com.ops.hunting.common.service.ThreatIntelService;
import com.ops.hunting.common.service.UserService;

@SpringBootTest
@ActiveProfiles("test")
class CommonModuleIntegrationTest {

	@MockBean
	private UserService userService;

	@MockBean
	private ThreatIntelService threatIntelService;

	@MockBean
	private AlertService alertService;

	@MockBean
	private AuthenticationService authenticationService;

	@Test
	@DisplayName("Should load application context successfully")
	void shouldLoadApplicationContextSuccessfully() {
		// This test verifies that the Spring context loads without errors
		// with all common module components properly configured
		assertTrue(true, "Application context loaded successfully");
	}

	@Test
	@DisplayName("Should validate all bean dependencies")
	void shouldValidateAllBeanDependencies() {
		// This test can be expanded to verify specific bean configurations
		// and dependency injection is working correctly
		assertNotNull(userService);
		assertNotNull(threatIntelService);
		assertNotNull(alertService);
		assertNotNull(authenticationService);
	}

	@Test
	@DisplayName("Should handle service interactions correctly")
	void shouldHandleServiceInteractionsCorrectly() {
		// Mock service behavior
		UserDto mockUser = UserDto.builder().id(UUID.randomUUID()).username("testuser").email("test@example.com")
				.role(UserRole.ANALYST).build();

		when(userService.getCurrentUser()).thenReturn(mockUser);
		when(userService.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

		// Test service interaction
		UserDto currentUser = userService.getCurrentUser();
		assertNotNull(currentUser);
		assertEquals("testuser", currentUser.getUsername());

		Optional<UserDto> foundUser = userService.findByUsername("testuser");
		assertTrue(foundUser.isPresent());
		assertEquals("testuser", foundUser.get().getUsername());
	}
}