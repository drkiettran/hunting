package com.ops.hunting.knowledge.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ArtifactTagId implements Serializable {

	@Column(name = "artifact_id")
	private UUID artifactId;

	@Column(name = "tag_id")
	private UUID tagId;

	// Constructors
	public ArtifactTagId() {
	}

	public ArtifactTagId(UUID artifactId, UUID tagId) {
		this.artifactId = artifactId;
		this.tagId = tagId;
	}

	// Equals and hashCode
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ArtifactTagId that = (ArtifactTagId) o;
		return Objects.equals(artifactId, that.artifactId) && Objects.equals(tagId, that.tagId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(artifactId, tagId);
	}

	// Getters and setters
	public UUID getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(UUID artifactId) {
		this.artifactId = artifactId;
	}

	public UUID getTagId() {
		return tagId;
	}

	public void setTagId(UUID tagId) {
		this.tagId = tagId;
	}
}