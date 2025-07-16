package com.ops.hunting.common.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Validation exception for handling validation errors
 */
@Getter
@Setter
public class ValidationException extends BusinessException {

	private final Map<String, String> fieldErrors;

	public ValidationException() {
		super("Validation failed", "VALIDATION_ERROR");
		this.fieldErrors = new HashMap<>();
	}

	public ValidationException(String message) {
		super(message, "VALIDATION_ERROR");
		this.fieldErrors = new HashMap<>();
	}

	public ValidationException(Map<String, String> fieldErrors) {
		super("Validation failed", "VALIDATION_ERROR");
		this.fieldErrors = fieldErrors != null ? new HashMap<>(fieldErrors) : new HashMap<>();
	}

	public void addFieldError(String field, String error) {
		this.fieldErrors.put(field, error);
	}

	public boolean hasFieldError(String field) {
		return this.fieldErrors.containsKey(field);
	}

	public String getFieldError(String field) {
		return this.fieldErrors.get(field);
	}

	public boolean hasErrors() {
		return !this.fieldErrors.isEmpty();
	}
}