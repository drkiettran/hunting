package com.ops.hunting.products.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.ops.hunting.common.enums.ArtifactType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ArtifactDto {

	private String id;

	@NotBlank(message = "Name is required")
	private String name;

	@NotNull(message = "Type is required")
	private ArtifactType type;

	private String description;

	@NotBlank(message = "Created by is required")
	private String createdBy;

	private String investigationId;
	private Long fileSize;
	private String contentType;
	private String storageLocation;
	private Boolean isArchived;
	private Map<String, String> metadata;
	private LocalDateTime createdDate;

	// Metrics
	private int tagCount;

	// Constructors
	public ArtifactDto() {
	}

	public ArtifactDto(String name, ArtifactType type, String description, String createdBy) {
		this.name = name;
		this.type = type;
		this.description = description;
		this.createdBy = createdBy;
	}

	// Getters and setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArtifactType getType() {
		return type;
	}

	public void setType(ArtifactType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getInvestigationId() {
		return investigationId;
	}

	public void setInvestigationId(String investigationId) {
		this.investigationId = investigationId;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public Boolean getIsArchived() {
		return isArchived;
	}

	public void setIsArchived(Boolean isArchived) {
		this.isArchived = isArchived;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public int getTagCount() {
		return tagCount;
	}

	public void setTagCount(int tagCount) {
		this.tagCount = tagCount;
	}
}