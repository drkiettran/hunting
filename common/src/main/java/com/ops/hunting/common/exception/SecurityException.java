package com.ops.hunting.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * Security exception for access control violations
 */
@Getter
@Setter
public class SecurityException extends BusinessException {

	private final String userId;
	private final String resource;
	private final String action;
	private final String ipAddress;

	public SecurityException(String message) {
		super(message, "SECURITY_ERROR");
		this.userId = null;
		this.resource = null;
		this.action = null;
		this.ipAddress = null;
	}

	public SecurityException(String message, String userId, String resource, String action) {
		super(message, "ACCESS_DENIED");
		this.userId = userId;
		this.resource = resource;
		this.action = action;
		this.ipAddress = null;
	}

	public SecurityException(String message, String userId, String resource, String action, String ipAddress) {
		super(message, "ACCESS_DENIED");
		this.userId = userId;
		this.resource = resource;
		this.action = action;
		this.ipAddress = ipAddress;
	}
}
