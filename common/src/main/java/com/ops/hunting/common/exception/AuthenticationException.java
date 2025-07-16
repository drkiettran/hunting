package com.ops.hunting.common.exception;

import lombok.Getter;

/**
 * Exception for authentication failures
 */
@Getter
public class AuthenticationException extends SecurityException {

	private final String username;
	private final String reason;

	public AuthenticationException(String message) {
		super(message);
		this.username = null;
		this.reason = null;
	}

	public AuthenticationException(String message, String username, String reason) {
		super(message);
		this.username = username;
		this.reason = reason;
	}
}