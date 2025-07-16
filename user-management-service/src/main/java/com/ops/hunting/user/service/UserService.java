package com.ops.hunting.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ops.hunting.common.dto.UserDto;
import com.ops.hunting.common.entity.User;
import com.ops.hunting.common.enums.AnalystTier;
import com.ops.hunting.common.enums.UserRole;
import com.ops.hunting.user.dto.LoginResponseDto;
import com.ops.hunting.user.dto.UserLoginDto;
import com.ops.hunting.user.dto.UserRegistrationDto;
import com.ops.hunting.user.entity.UserCredential;
import com.ops.hunting.user.repository.UserCredentialRepository;
import com.ops.hunting.user.repository.UserRepository;

@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;
	private final UserCredentialRepository credentialRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Autowired
	public UserService(UserRepository userRepository, UserCredentialRepository credentialRepository,
			PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.credentialRepository = credentialRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public UserDto createUser(UserRegistrationDto registrationDto) {
		// Check if username or email already exists
		if (userRepository.existsByUsername(registrationDto.getUsername())) {
			throw new RuntimeException("Username already exists");
		}
		if (userRepository.existsByEmail(registrationDto.getEmail())) {
			throw new RuntimeException("Email already exists");
		}

		// Create user entity
		User user = new User();
		user.setUsername(registrationDto.getUsername());
		user.setEmail(registrationDto.getEmail());
		user.setRole(registrationDto.getRole());
		// user.setTier(registrationDto.getTier());
		// user.setDepartment(registrationDto.getDepartment());
		// user.setClearanceLevel(registrationDto.getClearanceLevel());
		user.setIsActive(true);

		User savedUser = userRepository.save(user);

		// Create credentials
		UserCredential credential = new UserCredential();
		credential.setUserId(savedUser.getId());
		credential.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
		credentialRepository.save(credential);

		return convertToDto(savedUser);
	}

	public LoginResponseDto authenticateUser(UserLoginDto loginDto) {
		Optional<User> userOpt = userRepository.findByUsername(loginDto.getUsername());
		if (userOpt.isEmpty()) {
			throw new RuntimeException("Invalid credentials");
		}

		User user = userOpt.get();
		if (!user.getIsActive()) {
			throw new RuntimeException("Account is disabled");
		}

		Optional<UserCredential> credentialOpt = credentialRepository.findByUserId(user.getId().toString());
		if (credentialOpt.isEmpty()) {
			throw new RuntimeException("Invalid credentials");
		}

		UserCredential credential = credentialOpt.get();

		// Check if account is locked
		if (credential.getAccountLocked() && credential.getLockedUntil() != null
				&& credential.getLockedUntil().isAfter(LocalDateTime.now())) {
			throw new RuntimeException("Account is temporarily locked");
		}

		// Verify password
		if (!passwordEncoder.matches(loginDto.getPassword(), credential.getPasswordHash())) {
			// Increment failed attempts
			credential.setFailedLoginAttempts(credential.getFailedLoginAttempts() + 1);
			if (credential.getFailedLoginAttempts() >= 5) {
				credential.setAccountLocked(true);
				credential.setLockedUntil(LocalDateTime.now().plusMinutes(30));
			}
			credentialRepository.save(credential);
			throw new RuntimeException("Invalid credentials");
		}

		// Reset failed attempts on successful login
		credential.setFailedLoginAttempts(0);
		credential.setAccountLocked(false);
		credential.setLockedUntil(null);
		credentialRepository.save(credential);

		// Update last login
		user.setLastLogin(LocalDateTime.now());
		userRepository.save(user);

		// Generate JWT token
		List<String> roles = List.of(user.getRole().name());
		String token = jwtService.generateToken(user.getUsername(), roles);

		return new LoginResponseDto(token, convertToDto(user));
	}

	@Cacheable(value = "users", key = "#id")
	public Optional<UserDto> getUserById(String id) {
		return userRepository.findById(id).map(this::convertToDto);
	}

	@Cacheable(value = "users", key = "#username")
	public Optional<UserDto> getUserByUsername(String username) {
		return userRepository.findByUsername(username).map(this::convertToDto);
	}

	public Page<UserDto> getAllUsers(Pageable pageable) {
		return userRepository.findByIsActiveTrue(pageable).map(this::convertToDto);
	}

	public Page<UserDto> searchUsers(String search, Pageable pageable) {
		return userRepository.findActiveUsersBySearch(search, pageable).map(this::convertToDto);
	}

	public List<UserDto> getUsersByRole(UserRole role) {
		return userRepository.findByRoleAndIsActiveTrue(role).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<UserDto> getUsersByTier(AnalystTier tier) {
		return userRepository.findByTierAndIsActiveTrue(tier).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@CacheEvict(value = "users", key = "#id")
	@Transactional
	public UserDto updateUser(String id, UserDto userDto) {
		Optional<User> userOpt = userRepository.findById(id);
		if (userOpt.isEmpty()) {
			throw new RuntimeException("User not found");
		}

		User user = userOpt.get();
		user.setEmail(userDto.getEmail());
		user.setRole(userDto.getRole());
//		user.setTier(userDto.getTier());
//		user.setDepartment(userDto.getDepartment());
//		user.setClearanceLevel(userDto.getClearanceLevel());
		user.setIsActive(userDto.getIsActive());

		User savedUser = userRepository.save(user);
		return convertToDto(savedUser);
	}

	@CacheEvict(value = "users", key = "#id")
	@Transactional
	public void deactivateUser(String id) {
		Optional<User> userOpt = userRepository.findById(id);
		if (userOpt.isEmpty()) {
			throw new RuntimeException("User not found");
		}

		User user = userOpt.get();
		user.setIsActive(false);
		userRepository.save(user);
	}

	@Transactional
	public void changePassword(UUID uuid, String oldPassword, String newPassword) {
		Optional<UserCredential> credentialOpt = credentialRepository.findByUserId(uuid.toString());
		if (credentialOpt.isEmpty()) {
			throw new RuntimeException("User credentials not found");
		}

		UserCredential credential = credentialOpt.get();

		if (!passwordEncoder.matches(oldPassword, credential.getPasswordHash())) {
			throw new RuntimeException("Current password is incorrect");
		}

		credential.setPasswordHash(passwordEncoder.encode(newPassword));
		credential.setLastPasswordChange(LocalDateTime.now());
		credentialRepository.save(credential);
	}

	private UserDto convertToDto(User user) {
		UserDto dto = new UserDto();
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setEmail(user.getEmail());
		dto.setRole(user.getRole());
		dto.setIsActive(user.getIsActive());
		dto.setLastLogin(user.getLastLogin());
//		dto.setTier(user.getTier());
//		dto.setDepartment(user.getDepartment());
//		dto.setClearanceLevel(user.getClearanceLevel());
		dto.setCreatedAt(user.getCreatedAt());
		return dto;
	}
}
