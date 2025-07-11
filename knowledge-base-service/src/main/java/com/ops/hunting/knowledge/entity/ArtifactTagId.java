package com.ops.hunting.knowledge.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ArtifactTagId implements Serializable {

	@Column(name = "artifact_id")
	private String artifactId;

	@Column(name = "tag_id")
	private String tagId;

	// Constructors
	public ArtifactTagId() {
	}

	public ArtifactTagId(String artifactId, String tagId) {
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
	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
}