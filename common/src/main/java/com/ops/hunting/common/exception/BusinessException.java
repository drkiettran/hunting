package com.ops.hunting.common.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Base business exception for the application
 */
@Getter
@Setter
public class BusinessException extends RuntimeException {

	private final String errorCode;
	private final Map<String, Object> metadata;

	public BusinessException(String message) {
		super(message);
		this.errorCode = "BUSINESS_ERROR";
		this.metadata = new HashMap<>();
	}

	public BusinessException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
		this.metadata = new HashMap<>();
	}

	public BusinessException(String message, String errorCode, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
		this.metadata = new HashMap<>();
	}

	public BusinessException(String message, String errorCode, Map<String, Object> metadata) {
		super(message);
		this.errorCode = errorCode;
		this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
	}

	public void addMetadata(String key, Object value) {
		this.metadata.put(key, value);
	}
}