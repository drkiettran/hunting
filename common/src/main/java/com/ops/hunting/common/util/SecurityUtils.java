package com.ops.hunting.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ops.hunting.common.security.UserPrincipal;

//import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;

// ===================================================================
// 1. SECURITY UTILITIES
// ===================================================================

@Slf4j
public class SecurityUtils {

	private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final String TOKEN_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	// IP Address validation patterns
	private static final Pattern IPV4_PATTERN = Pattern.compile("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$");

	private static final Pattern IPV6_PATTERN = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|"
			+ "^::1$|^::$|" + "^(?:[0-9a-fA-F]{1,4}:){1,7}:$|" + "^:(?:[0-9a-fA-F]{1,4}:){1,6}[0-9a-fA-F]{1,4}$|"
			+ "^(?:[0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}$");

	// XSS prevention patterns
	private static final Pattern XSS_PATTERN = Pattern.compile(
			"(<script[^>]*>.*?</script>)|(<[^>]*javascript:[^>]*>)|(<[^>]*on\\w+\\s*=)",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private SecurityUtils() {
		// Utility class
	}

	/**
	 * Hash a password using BCrypt
	 */
	public static String hashPassword(String password) {
		if (password == null || password.trim().isEmpty()) {
			throw new IllegalArgumentException("Password cannot be null or empty");
		}
		return PASSWORD_ENCODER.encode(password);
	}

	/**
	 * Verify a password against its hash
	 */
	public static boolean verifyPassword(String password, String hashedPassword) {
		if (password == null || hashedPassword == null) {
			return false;
		}
		return PASSWORD_ENCODER.matches(password, hashedPassword);
	}

	/**
	 * Generate a secure random token
	 */
	public static String generateToken() {
		return generateToken(32);
	}

	/**
	 * Generate a secure random token of specified length
	 */
	public static String generateToken(int length) {
		StringBuilder token = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			token.append(TOKEN_CHARS.charAt(SECURE_RANDOM.nextInt(TOKEN_CHARS.length())));
		}
		return token.toString();
	}

	/**
	 * Validate IP address format (IPv4 or IPv6)
	 */
	public static boolean isValidIpAddress(String ipAddress) {
		if (ipAddress == null || ipAddress.trim().isEmpty()) {
			return false;
		}

		// Remove whitespace
		ipAddress = ipAddress.trim();

		// Check IPv4
		if (IPV4_PATTERN.matcher(ipAddress).matches()) {
			return true;
		}

		// Check IPv6
		if (IPV6_PATTERN.matcher(ipAddress).matches()) {
			return true;
		}

		// Try InetAddress validation as fallback
		try {
			InetAddress.getByName(ipAddress);
			return true;
		} catch (UnknownHostException e) {
			return false;
		}
	}

	/**
	 * Sanitize input to prevent XSS attacks
	 */
	public static String sanitizeInput(String input) {
		if (input == null) {
			return null;
		}

		// Remove potential XSS patterns
		String sanitized = XSS_PATTERN.matcher(input).replaceAll("");

		// Escape HTML characters
		sanitized = sanitized.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;")
				.replace("&", "&amp;");

		return sanitized.trim();
	}

	/**
	 * Validate password strength
	 */
	public static boolean isStrongPassword(String password) {
		if (password == null || password.length() < 8) {
			return false;
		}

		boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
		boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
		boolean hasDigit = password.chars().anyMatch(Character::isDigit);
		boolean hasSpecial = password.chars().anyMatch(c -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(c) >= 0);

		return hasUpper && hasLower && hasDigit && hasSpecial;
	}

	/**
	 * Generate a secure session ID
	 */
	public static String generateSessionId() {
		return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
	}

	/**
	 * Mask sensitive data for logging
	 */
	public static String maskSensitiveData(String data) {
		if (data == null || data.length() <= 4) {
			return "****";
		}

		int visibleChars = Math.min(2, data.length() / 4);
		String prefix = data.substring(0, visibleChars);
		String suffix = data.substring(data.length() - visibleChars);

		return prefix + "****" + suffix;
	}

	/**
	 * Get the current authenticated user's username
	 */
	public static Optional<String> getCurrentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();

			if (principal instanceof UserDetails) {
				return Optional.of(((UserDetails) principal).getUsername());
			} else if (principal instanceof String) {
				return Optional.of((String) principal);
			}
		}

		return Optional.empty();
	}

	/**
	 * Get the current authenticated user
	 */
	public static Optional<UserPrincipal> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();

			if (principal instanceof UserPrincipal) {
				return Optional.of((UserPrincipal) principal);
			}
		}

		return Optional.empty();
	}

	/**
	 * Get the current authentication
	 */
	public static Optional<Authentication> getCurrentAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return Optional.ofNullable(authentication);
	}

	/**
	 * Check if current user has a specific role
	 */
	public static boolean hasRole(String role) {
		return getCurrentAuthentication().map(auth -> auth.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role))).orElse(false);
	}

	/**
	 * Check if current user has any of the specified roles
	 */
	public static boolean hasAnyRole(String... roles) {
		for (String role : roles) {
			if (hasRole(role)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if current user has a specific authority
	 */
	public static boolean hasAuthority(String authority) {
		return getCurrentAuthentication().map(auth -> auth.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority))).orElse(false);
	}

	/**
	 * Check if user is authenticated
	 */
	public static boolean isAuthenticated() {
		return getCurrentAuthentication().map(Authentication::isAuthenticated).orElse(false);
	}

	/**
	 * Check if user is anonymous
	 */
	public static boolean isAnonymous() {
		return !isAuthenticated();
	}
}
