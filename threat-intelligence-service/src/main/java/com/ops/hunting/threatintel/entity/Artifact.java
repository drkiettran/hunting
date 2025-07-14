// ===================================
// 1. Artifact Entity (JPA + Elasticsearch)
// ===================================
package com.ops.hunting.threatintel.entity;

//import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "artifacts")
@Document(indexName = "hunting-artifacts")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Artifact {

	@Id
	@jakarta.persistence.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Field(type = FieldType.Keyword)
	@Column(unique = true, nullable = false)
	@NotBlank(message = "Artifact ID cannot be blank")
	private String artifactId;

	@Field(type = FieldType.Keyword)
	@Column(nullable = false)
	@NotNull(message = "Artifact type is required")
	@Enumerated(EnumType.STRING)
	private ArtifactType artifactType;

	@Field(type = FieldType.Text, analyzer = "standard")
	@Column(columnDefinition = "TEXT")
	private String description;

	@Field(type = FieldType.Keyword)
	@Column(nullable = false)
	@NotNull(message = "Source is required")
	private String source;

	@Field(type = FieldType.Keyword)
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ConfidenceLevel confidence = ConfidenceLevel.MEDIUM;

	@Field(type = FieldType.Keyword)
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TlpMarking tlpMarking = TlpMarking.AMBER;

	@Field(type = FieldType.Date, format = DateFormat.date_time)
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Column(nullable = false)
	private LocalDateTime timestamp;

	@Field(type = FieldType.Date, format = DateFormat.date_time)
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Column(nullable = false)
	private LocalDateTime ingestionTime;

	@Field(type = FieldType.Date, format = DateFormat.date_time)
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime expirationTime;

	// Intelligence-specific fields
	@Field(type = FieldType.Keyword)
	private String iocValue;

	@Field(type = FieldType.Keyword)
	@Enumerated(EnumType.STRING)
	private IocType iocType;

	@Field(type = FieldType.Keyword)
	private String threatActor;

	@Field(type = FieldType.Keyword)
	private String malwareFamily;

	@Field(type = FieldType.Keyword)
	private String campaign;

	@Field(type = FieldType.Keyword)
	private List<String> tags;

	@Field(type = FieldType.Keyword)
	private List<String> mitreAttackTechniques;

	// Metadata and raw data
	@Field(type = FieldType.Object, enabled = false)
	@Column(columnDefinition = "JSON")
	private Map<String, Object> metadata;

	@Field(type = FieldType.Object, enabled = false)
	@Column(columnDefinition = "JSON")
	private Map<String, Object> rawData;

	@Field(type = FieldType.Keyword)
	@Column(nullable = false)
	private String huntingSystem = "persistent-hunting-v1";

	@Field(type = FieldType.Boolean)
	@Column(nullable = false)
	private Boolean active = true;

	@Field(type = FieldType.Keyword)
	private String createdBy;

	@Field(type = FieldType.Keyword)
	private String updatedBy;

	@Field(type = FieldType.Date, format = DateFormat.date_time)
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Field(type = FieldType.Date, format = DateFormat.date_time)
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	// Constructors
	public Artifact() {
		this.timestamp = LocalDateTime.now();
		this.ingestionTime = LocalDateTime.now();
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Artifact(String artifactId, ArtifactType artifactType, String source) {
		this();
		this.artifactId = artifactId;
		this.artifactType = artifactType;
		this.source = source;
	}

	// Getters and Setters
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

	public Map<String, Object> getRawData() {
		return rawData;
	}

	public void setRawData(Map<String, Object> rawData) {
		this.rawData = rawData;
	}

	public String getHuntingSystem() {
		return huntingSystem;
	}

	public void setHuntingSystem(String huntingSystem) {
		this.huntingSystem = huntingSystem;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
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

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return "Artifact{" + "id=" + id + ", artifactId='" + artifactId + '\'' + ", artifactType=" + artifactType
				+ ", source='" + source + '\'' + ", confidence=" + confidence + ", tlpMarking=" + tlpMarking
				+ ", timestamp=" + timestamp + '}';
	}
}
