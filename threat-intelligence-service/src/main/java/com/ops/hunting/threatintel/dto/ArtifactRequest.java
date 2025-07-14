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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtifactRequest {

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

	public Map<String, Object> getRawData() {
		return rawData;
	}

	public void setRawData(Map<String, Object> rawData) {
		this.rawData = rawData;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public void setArtifactType(ArtifactType artifactType) {
		this.artifactType = artifactType;
	}

	@NotBlank(message = "Artifact ID cannot be blank")
	private String artifactId;

	@NotNull(message = "Artifact type is required")
	private ArtifactType artifactType;

	private String description;

	@NotNull(message = "Source is required")
	private String source;

	private ConfidenceLevel confidence = ConfidenceLevel.MEDIUM;
	private TlpMarking tlpMarking = TlpMarking.AMBER;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime timestamp;

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
	private Map<String, Object> rawData;

	public String getArtifactId() {
		return artifactId;
	}

	public ArtifactType getArtifactType() {
		return artifactType;
	}

}
