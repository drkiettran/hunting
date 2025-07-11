package com.ops.hunting.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper<T> {
	private boolean success;
	private String message;
	private T data;
	private String errorCode;
	private LocalDateTime timestamp;

	public ResponseWrapper() {
		this.timestamp = LocalDateTime.now();
	}

	public ResponseWrapper(boolean success, String message, T data) {
		this();
		this.success = success;
		this.message = message;
		this.data = data;
	}

	public static <T> ResponseWrapper<T> success(T data) {
		return new ResponseWrapper<>(true, "Operation successful", data);
	}

	public static <T> ResponseWrapper<T> success(String message, T data) {
		return new ResponseWrapper<>(true, message, data);
	}

	public static <T> ResponseWrapper<T> error(String message) {
		return new ResponseWrapper<>(false, message, null);
	}

	public static <T> ResponseWrapper<T> error(String message, String errorCode) {
		ResponseWrapper<T> response = new ResponseWrapper<>(false, message, null);
		response.setErrorCode(errorCode);
		return response;
	}

	// Getters and setters
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
