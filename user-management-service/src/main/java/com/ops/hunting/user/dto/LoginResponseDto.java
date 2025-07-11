package com.ops.hunting.user.dto;

import com.ops.hunting.common.dto.UserDto;

public class LoginResponseDto {

	private String token;
	private UserDto user;

	// Constructors
	public LoginResponseDto() {
	}

	public LoginResponseDto(String token, UserDto user) {
		this.token = token;
		this.user = user;
	}

	// Getters and setters
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}
}
