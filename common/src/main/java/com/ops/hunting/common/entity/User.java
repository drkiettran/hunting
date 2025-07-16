package com.ops.hunting.common.entity;

import java.time.LocalDateTime;

import com.ops.hunting.common.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@NotBlank(message = "Email is required")
	@Email(message = "Email format is invalid")
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@NotBlank(message = "Password is required")
	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@NotBlank(message = "First name is required")
	@Column(name = "first_name", nullable = false)
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Column(name = "last_name", nullable = false)
	private String lastName;

	@NotBlank(message = "Role is required")
	@Column(name = "role", nullable = false)
	private UserRole role;

	@Column(name = "is_active")
	@Builder.Default
	private Boolean isActive = true;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	@Column(name = "failed_login_attempts")
	@Builder.Default
	private Integer failedLoginAttempts = 0;

	@Column(name = "locked_until")
	private LocalDateTime lockedUntil;

	@Column(name = "password_reset_token")
	private String passwordResetToken;

	@Column(name = "password_reset_expires")
	private LocalDateTime passwordResetExpires;

	public boolean isLocked() {
		return lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil);
	}

	public boolean isPasswordResetTokenValid() {
		return passwordResetToken != null && passwordResetExpires != null
				&& LocalDateTime.now().isBefore(passwordResetExpires);
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}
}
