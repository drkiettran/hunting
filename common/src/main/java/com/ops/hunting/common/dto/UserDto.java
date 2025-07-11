package com.ops.hunting.common.dto;

import com.ops.hunting.common.enums.AnalystTier;
import com.ops.hunting.common.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class UserDto {
	private String id;

	@NotBlank
	private String username;

	@Email
	@NotBlank
	private String email;

	private UserRole role;
	private Boolean isActive;
	private LocalDateTime lastLogin;
	private AnalystTier tier;
	private String department;
	private String clearanceLevel;
	private LocalDateTime createdDate;

	// Constructors
	public UserDto() {
	}

	public UserDto(String id, String username, String email, UserRole role) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.role = role;
	}

	// Getters and setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public AnalystTier getTier() {
		return tier;
	}

	public void setTier(AnalystTier tier) {
		this.tier = tier;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getClearanceLevel() {
		return clearanceLevel;
	}

	public void setClearanceLevel(String clearanceLevel) {
		this.clearanceLevel = clearanceLevel;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
}
