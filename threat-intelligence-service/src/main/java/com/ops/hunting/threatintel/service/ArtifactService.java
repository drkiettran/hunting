package com.ops.hunting.threatintel.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ops.hunting.threatintel.dto.ArtifactRequest;
import com.ops.hunting.threatintel.dto.ArtifactResponse;
import com.ops.hunting.threatintel.entity.Artifact;
import com.ops.hunting.threatintel.entity.ArtifactType;
import com.ops.hunting.threatintel.repository.ArtifactElasticsearchRepository;
import com.ops.hunting.threatintel.repository.ArtifactRepository;

@Service
@Transactional
public class ArtifactService {

	@Autowired
	private ArtifactRepository artifactRepository;

	@Autowired
	private ArtifactElasticsearchRepository elasticsearchRepository;

	public ArtifactResponse createArtifact(ArtifactRequest request) {
		Artifact artifact = convertToEntity(request);

		// Generate artifact ID if not provided
		if (artifact.getArtifactId() == null || artifact.getArtifactId().isEmpty()) {
			artifact.setArtifactId(generateArtifactId());
		}

		// Save to database
		Artifact savedArtifact = artifactRepository.save(artifact);

		// Index in Elasticsearch
		elasticsearchRepository.save(savedArtifact);

		return convertToResponse(savedArtifact);
	}

	public Optional<ArtifactResponse> getArtifactById(String artifactId) {
		return artifactRepository.findByArtifactId(artifactId).map(this::convertToResponse);
	}

	public List<ArtifactResponse> getArtifactsByType(ArtifactType type) {
		return artifactRepository.findByArtifactTypeAndActiveTrue(type).stream().map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	public Page<ArtifactResponse> getAllArtifacts(Pageable pageable) {
		return artifactRepository.findAll(pageable).map(this::convertToResponse);
	}

	public ArtifactResponse updateArtifact(String artifactId, ArtifactRequest request) {
		Optional<Artifact> existingArtifact = artifactRepository.findByArtifactId(artifactId);

		if (existingArtifact.isPresent()) {
			Artifact artifact = existingArtifact.get();
			updateArtifactFromRequest(artifact, request);

			Artifact savedArtifact = artifactRepository.save(artifact);
			elasticsearchRepository.save(savedArtifact);

			return convertToResponse(savedArtifact);
		}

		throw new RuntimeException("Artifact not found: " + artifactId);
	}

	public void deleteArtifact(String artifactId) {
		Optional<Artifact> artifact = artifactRepository.findByArtifactId(artifactId);

		if (artifact.isPresent()) {
			// Soft delete
			Artifact art = artifact.get();
			art.setActive(false);
			artifactRepository.save(art);

			// Remove from Elasticsearch
			elasticsearchRepository.deleteById(artifactId);
		} else {
			throw new RuntimeException("Artifact not found: " + artifactId);
		}
	}

	public List<ArtifactResponse> searchArtifacts(String query) {
		// Implement Elasticsearch search logic here
		// This is a simplified version
		return elasticsearchRepository.findByIocValue(query).stream().map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	private String generateArtifactId() {
		return "ART-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	private Artifact convertToEntity(ArtifactRequest request) {
		Artifact artifact = new Artifact();
		artifact.setArtifactId(request.getArtifactId());
		artifact.setArtifactType(request.getArtifactType());
		artifact.setDescription(request.getDescription());
		artifact.setSource(request.getSource());
		artifact.setConfidence(request.getConfidence());
		artifact.setTlpMarking(request.getTlpMarking());
		artifact.setTimestamp(request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now());
		artifact.setExpirationTime(request.getExpirationTime());
		artifact.setIocValue(request.getIocValue());
		artifact.setIocType(request.getIocType());
		artifact.setThreatActor(request.getThreatActor());
		artifact.setMalwareFamily(request.getMalwareFamily());
		artifact.setCampaign(request.getCampaign());
		artifact.setTags(request.getTags());
		artifact.setMitreAttackTechniques(request.getMitreAttackTechniques());
		artifact.setMetadata(request.getMetadata());
		artifact.setRawData(request.getRawData());

		return artifact;
	}

	private ArtifactResponse convertToResponse(Artifact artifact) {
		ArtifactResponse response = new ArtifactResponse();
		response.setId(artifact.getId());
		response.setArtifactId(artifact.getArtifactId());
		response.setArtifactType(artifact.getArtifactType());
		response.setDescription(artifact.getDescription());
		response.setSource(artifact.getSource());
		response.setConfidence(artifact.getConfidence());
		response.setTlpMarking(artifact.getTlpMarking());
		response.setTimestamp(artifact.getTimestamp());
		response.setIngestionTime(artifact.getIngestionTime());
		response.setExpirationTime(artifact.getExpirationTime());
		response.setIocValue(artifact.getIocValue());
		response.setIocType(artifact.getIocType());
		response.setThreatActor(artifact.getThreatActor());
		response.setMalwareFamily(artifact.getMalwareFamily());
		response.setCampaign(artifact.getCampaign());
		response.setTags(artifact.getTags());
		response.setMitreAttackTechniques(artifact.getMitreAttackTechniques());
		response.setMetadata(artifact.getMetadata());
		response.setActive(artifact.getActive());
		response.setCreatedAt(artifact.getCreatedAt());
		response.setUpdatedAt(artifact.getUpdatedAt());

		return response;
	}

	private void updateArtifactFromRequest(Artifact artifact, ArtifactRequest request) {
		if (request.getDescription() != null)
			artifact.setDescription(request.getDescription());
		if (request.getConfidence() != null)
			artifact.setConfidence(request.getConfidence());
		if (request.getTlpMarking() != null)
			artifact.setTlpMarking(request.getTlpMarking());
		if (request.getExpirationTime() != null)
			artifact.setExpirationTime(request.getExpirationTime());
		if (request.getIocValue() != null)
			artifact.setIocValue(request.getIocValue());
		if (request.getIocType() != null)
			artifact.setIocType(request.getIocType());
		if (request.getThreatActor() != null)
			artifact.setThreatActor(request.getThreatActor());
		if (request.getMalwareFamily() != null)
			artifact.setMalwareFamily(request.getMalwareFamily());
		if (request.getCampaign() != null)
			artifact.setCampaign(request.getCampaign());
		if (request.getTags() != null)
			artifact.setTags(request.getTags());
		if (request.getMitreAttackTechniques() != null)
			artifact.setMitreAttackTechniques(request.getMitreAttackTechniques());
		if (request.getMetadata() != null)
			artifact.setMetadata(request.getMetadata());
		if (request.getRawData() != null)
			artifact.setRawData(request.getRawData());
	}
}
