package com.ops.hunting.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Application configuration properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "hunting.app")
@Validated
public class ApplicationConfigurationProperties {

	@NotBlank(message = "Application name is required")
	private String name = "Threat Hunting Platform";

	@NotBlank(message = "Application version is required")
	private String version = "1.0.0";

	private String environment = "development";

	private Boolean enableMetrics = true;

	private Boolean enableAuditLog = true;

	private Boolean enableCaching = true;

	@Min(value = 1, message = "Cache TTL must be positive")
	private Integer defaultCacheTtlSeconds = 3600;

	@Min(value = 1, message = "Max file size must be positive")
	private Long maxFileUploadSize = 52428800L; // 50MB

	private String supportEmail = "support@hunting.com";
}