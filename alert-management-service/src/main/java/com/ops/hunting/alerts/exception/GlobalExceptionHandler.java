package com.ops.hunting.alerts.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(AlertNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleAlertNotFound(AlertNotFoundException ex) {
		log.error("Alert not found: {}", ex.getMessage());

		ErrorResponse error = ErrorResponse.builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.NOT_FOUND.value()).error("Not Found").message(ex.getMessage()).path("/api/alerts")
				.build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		log.error("Validation error: {}", ex.getMessage());

		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		ErrorResponse error = ErrorResponse.builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.BAD_REQUEST.value()).error("Validation Failed").message("Invalid input")
				.path("/api/alerts").validationErrors(errors).build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
		log.error("Access denied: {}", ex.getMessage());

		ErrorResponse error = ErrorResponse.builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.FORBIDDEN.value()).error("Access Denied")
				.message("You don't have permission to access this resource").path("/api/alerts").build();

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		log.error("Unexpected error: ", ex);

		ErrorResponse error = ErrorResponse.builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).error("Internal Server Error")
				.message("An unexpected error occurred").path("/api/alerts").build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}