package com.ops.hunting.threatintel.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ops.hunting.threatintel.entity.ArtifactType;
import com.ops.hunting.threatintel.entity.ConfidenceLevel;
import com.ops.hunting.threatintel.entity.IocType;
import com.ops.hunting.threatintel.entity.TlpMarking;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtifactResponse {
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public ArtifactType getArtifactType() {
		return artifactType;
	}

	public void setArtifactType(ArtifactType artifactType) {
		this.artifactType = artifactType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public ConfidenceLevel getConfidence() {
		return confidence;
	}

	public void setConfidence(ConfidenceLevel confidence) {
		this.confidence = confidence;
	}

	public TlpMarking getTlpMarking() {
		return tlpMarking;
	}

	public void setTlpMarking(TlpMarking tlpMarking) {
		this.tlpMarking = tlpMarking;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public LocalDateTime getIngestionTime() {
		return ingestionTime;
	}

	public void setIngestionTime(LocalDateTime ingestionTime) {
		this.ingestionTime = ingestionTime;
	}

	public LocalDateTime getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(LocalDateTime expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getIocValue() {
		return iocValue;
	}

	public void setIocValue(String iocValue) {
		this.iocValue = iocValue;
	}

	public IocType getIocType() {
		return iocType;
	}

	public void setIocType(IocType iocType) {
		this.iocType = iocType;
	}

	public String getThreatActor() {
		return threatActor;
	}

	public void setThreatActor(String threatActor) {
		this.threatActor = threatActor;
	}

	public String getMalwareFamily() {
		return malwareFamily;
	}

	public void setMalwareFamily(String malwareFamily) {
		this.malwareFamily = malwareFamily;
	}

	public String getCampaign() {
		return campaign;
	}

	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<String> getMitreAttackTechniques() {
		return mitreAttackTechniques;
	}

	public void setMitreAttackTechniques(List<String> mitreAttackTechniques) {
		this.mitreAttackTechniques = mitreAttackTechniques;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	private Long id;
	private String artifactId;
	private ArtifactType artifactType;
	private String description;
	private String source;
	private ConfidenceLevel confidence;
	private TlpMarking tlpMarking;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime timestamp;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime ingestionTime;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime expirationTime;

	private String iocValue;
	private IocType iocType;
	private String threatActor;
	private String malwareFamily;
	private String campaign;
	private List<String> tags;
	private List<String> mitreAttackTechniques;
	private Map<String, Object> metadata;
	private Boolean active;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime updatedAt;

	// Getters and setters (same pattern as entity)
	// ... (omitted for brevity)
}
