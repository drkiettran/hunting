package com.ops.hunting.knowledge.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "artifact_tags")
public class ArtifactTag {

	@EmbeddedId
	private ArtifactTagId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("artifactId")
	@JoinColumn(name = "artifact_id")
	private Artifact artifact;

	@Column(name = "tag_id")
	private String tagId;

	@Column(name = "tag_name")
	private String tagName;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@PrePersist
	protected void onCreate() {
		createdDate = LocalDateTime.now();
	}

	// Constructors
	public ArtifactTag() {
	}

	public ArtifactTag(Artifact artifact, String tagId, String tagName) {
		this.id = new ArtifactTagId(artifact.getId(), tagId);
		this.artifact = artifact;
		this.tagId = tagId;
		this.tagName = tagName;
	}

	// Getters and setters
	public ArtifactTagId getId() {
		return id;
	}

	public void setId(ArtifactTagId id) {
		this.id = id;
	}

	public Artifact getArtifact() {
		return artifact;
	}

	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
}