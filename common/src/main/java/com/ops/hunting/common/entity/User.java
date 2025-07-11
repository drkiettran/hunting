package com.ops.hunting.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import com.ops.hunting.common.enums.AnalystTier;
import com.ops.hunting.common.enums.UserRole;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

	@NotBlank
	@Column(unique = true, nullable = false)
	private String username;

	@Email
	@NotBlank
	@Column(unique = true, nullable = false)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	@Enumerated(EnumType.STRING)
	private AnalystTier tier;

	private String department;

	@Column(name = "clearance_level")
	private String clearanceLevel;

	// Constructors
	public User() {
	}

	public User(String username, String email, UserRole role) {
		this.username = username;
		this.email = email;
		this.role = role;
	}

	// Getters and setters
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
}
