package com.ops.hunting.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
	private String token;
	private String refreshToken;
	private String username;
	private Long expiresIn;

	public LoginResponse(String token, String username, Long expiresIn) {
		this.token = token;
		this.username = username;
		this.expiresIn = expiresIn;
	}
}