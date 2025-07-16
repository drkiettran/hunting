package com.ops.hunting.common.service;

import java.util.Optional;
import java.util.UUID;

import com.ops.hunting.common.dto.PasswordChangeRequest;
import com.ops.hunting.common.dto.PasswordResetRequest;
import com.ops.hunting.common.dto.UserDto;
import com.ops.hunting.common.dto.UserRequest;
import com.ops.hunting.common.dto.UserUpdateRequest;

// ===================================================================
// 1. USER SERVICE INTERFACE
// ===================================================================

public interface UserService {

	/**
	 * Get current authenticated user
	 */
	UserDto getCurrentUser();

	/**
	 * Find user by username
	 */
	Optional<UserDto> findByUsername(String username);

	/**
	 * Find user by email
	 */
	Optional<UserDto> findByEmail(String email);

	/**
	 * Create new user
	 */
	UserDto createUser(UserRequest request);

	/**
	 * Update existing user
	 */
	UserDto updateUser(UUID userId, UserUpdateRequest request);

	/**
	 * Delete user
	 */
	void deleteUser(UUID userId);

	/**
	 * Authenticate user
	 */
	Optional<UserDto> authenticate(String username, String password);

	/**
	 * Lock user account
	 */
	void lockUser(UUID userId, String reason);

	/**
	 * Unlock user account
	 */
	void unlockUser(UUID userId);

	/**
	 * Change user password
	 */
	void changePassword(UUID userId, PasswordChangeRequest request);

	/**
	 * Reset user password
	 */
	void resetPassword(PasswordResetRequest request);
}
