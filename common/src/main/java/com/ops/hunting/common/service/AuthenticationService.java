package com.ops.hunting.common.service;

import java.util.Optional;

import com.ops.hunting.common.dto.LoginRequest;
import com.ops.hunting.common.dto.LoginResponse;
import com.ops.hunting.common.dto.UserDto;

public interface AuthenticationService {

	/**
	 * Authenticate user and return token
	 */
	LoginResponse authenticate(LoginRequest request);

	/**
	 * Validate JWT token
	 */
	boolean validateToken(String token);

	/**
	 * Get user from token
	 */
	Optional<UserDto> getUserFromToken(String token);

	/**
	 * Refresh token
	 */
	String refreshToken(String token);

	/**
	 * Logout user
	 */
	void logout(String token);
}