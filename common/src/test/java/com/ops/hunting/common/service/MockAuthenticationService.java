package com.ops.hunting.common.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.ops.hunting.common.dto.LoginRequest;
import com.ops.hunting.common.dto.LoginResponse;
import com.ops.hunting.common.dto.UserDto;
import com.ops.hunting.common.util.SecurityUtils;

/**
 * Mock AuthenticationService implementation for testing
 */
public class MockAuthenticationService implements AuthenticationService {

	private final Set<String> validTokens = new HashSet<>();
	private final UserService userService;

	public MockAuthenticationService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public LoginResponse authenticate(LoginRequest request) {
		Optional<UserDto> user = userService.findByUsername(request.getUsername());
		if (user.isPresent()) {
			String token = SecurityUtils.generateToken();
			validTokens.add(token);

			return LoginResponse.builder().token(token).tokenType("Bearer").expiresAt(LocalDateTime.now().plusHours(24))
					.user(user.get()).permissions(Set.of("READ", "WRITE")).build();
		}
		return null;
	}

	@Override
	public boolean validateToken(String token) {
		return validTokens.contains(token);
	}

	@Override
	public Optional<UserDto> getUserFromToken(String token) {
		if (validateToken(token)) {
			return Optional.of(userService.getCurrentUser());
		}
		return Optional.empty();
	}

	@Override
	public String refreshToken(String token) {
		if (validateToken(token)) {
			String newToken = SecurityUtils.generateToken();
			validTokens.remove(token);
			validTokens.add(newToken);
			return newToken;
		}
		return null;
	}

	@Override
	public void logout(String token) {
		validTokens.remove(token);
	}
}