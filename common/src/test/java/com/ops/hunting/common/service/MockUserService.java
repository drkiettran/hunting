package com.ops.hunting.common.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.ops.hunting.common.dto.PasswordChangeRequest;
import com.ops.hunting.common.dto.PasswordResetRequest;
import com.ops.hunting.common.dto.UserDto;
import com.ops.hunting.common.dto.UserRequest;
import com.ops.hunting.common.dto.UserUpdateRequest;
import com.ops.hunting.common.enums.UserRole;

/**
 * Mock UserService implementation for testing
 */
public class MockUserService implements UserService {

	private final Map<String, UserDto> users = new HashMap<>();
	private UserDto currentUser;

	public MockUserService() {
		// Add default test user
		currentUser = UserDto.builder().id(UUID.randomUUID()).username("testuser").email("test@example.com")
				.firstName("Test").lastName("User").role(UserRole.ANALYST).isActive(true).build();
		users.put("testuser", currentUser);
	}

	@Override
	public UserDto getCurrentUser() {
		return currentUser;
	}

	@Override
	public Optional<UserDto> findByUsername(String username) {
		return Optional.ofNullable(users.get(username));
	}

	@Override
	public Optional<UserDto> findByEmail(String email) {
		return users.values().stream().filter(user -> email.equals(user.getEmail())).findFirst();
	}

	@Override
	public UserDto createUser(UserRequest request) {
		UserDto user = UserDto.builder().id(UUID.randomUUID()).username(request.getUsername()).email(request.getEmail())
				.firstName(request.getFirstName()).lastName(request.getLastName()).role(request.getRole())
				.isActive(true).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

		users.put(user.getUsername(), user);
		return user;
	}

	@Override
	public UserDto updateUser(UUID userId, UserUpdateRequest request) {
		// Mock implementation
		return currentUser;
	}

	@Override
	public void deleteUser(UUID userId) {
		// Mock implementation
	}

	@Override
	public Optional<UserDto> authenticate(String username, String password) {
		return findByUsername(username);
	}

	@Override
	public void lockUser(UUID userId, String reason) {
		// Mock implementation
	}

	@Override
	public void unlockUser(UUID userId) {
		// Mock implementation
	}

	@Override
	public void changePassword(UUID userId, PasswordChangeRequest request) {
		// Mock implementation
	}

	@Override
	public void resetPassword(PasswordResetRequest request) {
		// Mock implementation
	}
}
