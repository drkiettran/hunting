package com.ops.hunting.knowledge.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ops.hunting.common.entity.BaseEntity;
import com.ops.hunting.common.enums.ArtifactType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "artifacts")
public class Artifact extends BaseEntity {

	@NotBlank
	@Column(nullable = false, length = 200)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ArtifactType type;

	@Column(name = "file_path", length = 500)
	private String filePath;

	@Column(columnDefinition = "TEXT")
	private String description;

	@NotBlank
	@Column(name = "created_by", nullable = false)
	private String createdBy;

	@Column(name = "investigation_id")
	private String investigationId;

	@Column(name = "file_size")
	private Long fileSize;

	@Column(name = "content_type")
	private String contentType;

	@Column(name = "checksum")
	private String checksum;

	@Column(name = "storage_location")
	private String storageLocation;

	@Column(name = "is_archived")
	private Boolean isArchived = false;

	@ElementCollection
	@CollectionTable(name = "artifact_metadata", joinColumns = @JoinColumn(name = "artifact_id"))
	@MapKeyColumn(name = "metadata_key")
	@Column(name = "metadata_value")
	private Map<String, String> metadata = new HashMap<>();

	@OneToMany(mappedBy = "artifact", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ArtifactTag> tags = new ArrayList<>();

	// Constructors
	public Artifact() {
	}

	public Artifact(String name, ArtifactType type, String description, String createdBy) {
		this.name = name;
		this.type = type;
		this.description = description;
		this.createdBy = createdBy;
	}

	// Business methods
	public void addMetadata(String key, String value) {
		this.metadata.put(key, value);
	}

	public String getMetadata(String key) {
		return this.metadata.get(key);
	}

	public void archive() {
		this.isArchived = true;
	}

	public boolean isLargeFile() {
		return fileSize != null && fileSize > 10 * 1024 * 1024; // 10MB
	}

	// Getters and setters
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

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
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

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
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

	public List<ArtifactTag> getTags() {
		return tags;
	}

	public void setTags(List<ArtifactTag> tags) {
		this.tags = tags;
	}
}
