package com.ops.hunting.common.exception;

import lombok.Getter;

/**
 * Exception for authorization failures
 */
@Getter
public class AuthorizationException extends SecurityException {

	private final String requiredRole;
	private final String userRole;

	public AuthorizationException(String message, String userId, String resource, String action) {
		super(message, userId, resource, action);
		this.requiredRole = null;
		this.userRole = null;
	}

	public AuthorizationException(String message, String userId, String resource, String action, String requiredRole,
			String userRole) {
		super(message, userId, resource, action);
		this.requiredRole = requiredRole;
		this.userRole = userRole;
	}
}