package com.ops.hunting.threatintel.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ops.hunting.common.enums.IndicatorType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IndicatorDto {

	private UUID id;

	@NotNull(message = "Indicator type is required")
	private IndicatorType type;

	@NotBlank(message = "Value is required")
	private String value;

	private String description;

	@DecimalMin(value = "0.0", message = "Confidence must be between 0 and 1")
	@DecimalMax(value = "1.0", message = "Confidence must be between 0 and 1")
	private BigDecimal confidence;

	private LocalDateTime createdDate;

	// Constructors
	public IndicatorDto() {
	}

	public IndicatorDto(IndicatorType type, String value, String description, BigDecimal confidence) {
		this.type = type;
		this.value = value;
		this.description = description;
		this.confidence = confidence;
	}

	// Getters and setters
	public UUID getId() {
		return id;
	}

	public void setId(UUID uuid) {
		this.id = uuid;
	}

	public IndicatorType getType() {
		return type;
	}

	public void setType(IndicatorType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getConfidence() {
		return confidence;
	}

	public void setConfidence(BigDecimal confidence) {
		this.confidence = confidence;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
}