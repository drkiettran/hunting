package com.ops.hunting.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Security configuration properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "hunting.security")
@Validated
public class SecurityConfigurationProperties {

	@NotBlank(message = "JWT secret is required")
	private String jwtSecret;

	@NotNull(message = "JWT expiration is required")
	@Min(value = 1, message = "JWT expiration must be positive")
	private Long jwtExpirationMs;

	@NotNull(message = "Max login attempts is required")
	@Min(value = 1, message = "Max login attempts must be positive")
	private Integer maxLoginAttempts;

	@NotNull(message = "Lockout duration is required")
	@Min(value = 1, message = "Lockout duration must be positive")
	private Long lockoutDurationMs;

	@NotNull(message = "Password reset expiry is required")
	@Min(value = 1, message = "Password reset expiry must be positive")
	private Long passwordResetExpiryMs;

	private Boolean enableTwoFactor = false;

	private Boolean enableSessionTimeout = true;

	@Min(value = 1, message = "Session timeout must be positive")
	private Long sessionTimeoutMs = 1800000L; // 30 minutes

	public void setMaxLoginAttempts(Integer maxLoginAttempts) {
		if (maxLoginAttempts != null && maxLoginAttempts <= 0) {
			throw new IllegalArgumentException("Max login attempts must be positive");
		}
		this.maxLoginAttempts = maxLoginAttempts;
	}

	public void setJwtExpirationMs(Long jwtExpirationMs) {
		if (jwtExpirationMs != null && jwtExpirationMs <= 0) {
			throw new IllegalArgumentException("JWT expiration must be positive");
		}
		this.jwtExpirationMs = jwtExpirationMs;
	}
}