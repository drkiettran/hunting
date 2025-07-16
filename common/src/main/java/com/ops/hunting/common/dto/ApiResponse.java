package com.ops.hunting.common.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

	private Boolean success;

	private String message;

	private T data;

	private java.util.List<String> errors;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime timestamp;

	public static <T> ApiResponse<T> success(T data) {
		return ApiResponse.<T>builder().success(true).data(data).timestamp(LocalDateTime.now()).build();
	}

	public static <T> ApiResponse<T> success(T data, String message) {

		return ApiResponse.<T>builder().success(true).message(message).data(data).timestamp(LocalDateTime.now())
				.build();
	}

	public static <T> ApiResponse<T> error(String message) {
		return ApiResponse.<T>builder().success(false).message(message).timestamp(LocalDateTime.now()).build();
	}

	public static <T> ApiResponse<T> error(java.util.List<String> errors) {
		return ApiResponse.<T>builder().success(false).errors(errors).timestamp(LocalDateTime.now()).build();
	}
}
