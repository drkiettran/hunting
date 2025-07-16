package com.ops.hunting.common.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

	@Email(message = "Email format is invalid")
	private String email;

	private String firstName;

	private String lastName;

	private String role;

	private Boolean isActive;
}
