package com.ops.hunting.common.security;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class PasswordPolicyValidator {

	private static final int MIN_LENGTH = 12;
	private static final int MAX_LENGTH = 128;

	private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
	private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
	private static final Pattern DIGIT = Pattern.compile("[0-9]");
	private static final Pattern SPECIAL = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

	// Common weak passwords
	private static final List<String> WEAK_PASSWORDS = List.of("password", "123456", "password123", "admin", "root",
			"user", "qwerty", "abc123", "password1", "hunting", "cybersecurity");

	public ValidationResult validate(String password) {
		List<String> errors = new ArrayList<>();

		if (password == null || password.trim().isEmpty()) {
			errors.add("Password cannot be empty");
			return new ValidationResult(false, errors);
		}

		// Length check
		if (password.length() < MIN_LENGTH) {
			errors.add("Password must be at least " + MIN_LENGTH + " characters long");
		}

		if (password.length() > MAX_LENGTH) {
			errors.add("Password must not exceed " + MAX_LENGTH + " characters");
		}

		// Character requirements
		if (!UPPERCASE.matcher(password).find()) {
			errors.add("Password must contain at least one uppercase letter");
		}

		if (!LOWERCASE.matcher(password).find()) {
			errors.add("Password must contain at least one lowercase letter");
		}

		if (!DIGIT.matcher(password).find()) {
			errors.add("Password must contain at least one digit");
		}

		if (!SPECIAL.matcher(password).find()) {
			errors.add("Password must contain at least one special character");
		}

		// Weak password check
		String lowerPassword = password.toLowerCase();
		if (WEAK_PASSWORDS.stream().anyMatch(lowerPassword::contains)) {
			errors.add("Password contains common weak patterns");
		}

		// Sequential characters check
		if (hasSequentialChars(password)) {
			errors.add("Password cannot contain sequential characters");
		}

		// Repeated characters check
		if (hasRepeatedChars(password, 3)) {
			errors.add("Password cannot contain more than 2 repeated characters");
		}

		return new ValidationResult(errors.isEmpty(), errors);
	}

	private boolean hasSequentialChars(String password) {
		for (int i = 0; i < password.length() - 2; i++) {
			char c1 = password.charAt(i);
			char c2 = password.charAt(i + 1);
			char c3 = password.charAt(i + 2);

			if (c2 == c1 + 1 && c3 == c2 + 1) {
				return true;
			}
		}
		return false;
	}

	private boolean hasRepeatedChars(String password, int maxRepeats) {
		int count = 1;
		for (int i = 1; i < password.length(); i++) {
			if (password.charAt(i) == password.charAt(i - 1)) {
				count++;
				if (count >= maxRepeats) {
					return true;
				}
			} else {
				count = 1;
			}
		}
		return false;
	}

	public static class ValidationResult {
		private final boolean valid;
		private final List<String> errors;

		public ValidationResult(boolean valid, List<String> errors) {
			this.valid = valid;
			this.errors = errors;
		}

		public boolean isValid() {
			return valid;
		}

		public List<String> getErrors() {
			return errors;
		}
	}
}
