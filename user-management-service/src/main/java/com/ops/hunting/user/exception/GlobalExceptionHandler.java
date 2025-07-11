package com.ops.hunting.user.exception;

import com.ops.hunting.common.util.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<Map<String, String>>> handleValidationExceptions(
			MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return ResponseEntity.badRequest().body(ResponseWrapper.error("Validation failed", "VALIDATION_ERROR"));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ResponseWrapper<Void>> handleRuntimeException(RuntimeException ex) {
		return ResponseEntity.badRequest().body(ResponseWrapper.error(ex.getMessage(), "RUNTIME_ERROR"));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseWrapper<Void>> handleGenericException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ResponseWrapper.error("An unexpected error occurred", "INTERNAL_ERROR"));
	}
}