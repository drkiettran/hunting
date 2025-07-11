package com.ops.hunting.knowledge.service;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//import org.elasticsearch.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ops.hunting.common.enums.ArtifactType;
import com.ops.hunting.knowledge.dto.ArtifactDto;
import com.ops.hunting.knowledge.entity.Artifact;
import com.ops.hunting.knowledge.repository.ArtifactRepository;

@Service
@Transactional
public class ArtifactService {

	private final ArtifactRepository artifactRepository;
	private final StorageService storageService;
	private final SearchService searchService;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	public ArtifactService(ArtifactRepository artifactRepository, StorageService storageService,
			SearchService searchService, KafkaTemplate<String, Object> kafkaTemplate) {
		this.artifactRepository = artifactRepository;
		this.storageService = storageService;
		this.searchService = searchService;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Transactional
	public ArtifactDto uploadArtifact(MultipartFile file, ArtifactDto dto) {
		try {
			// Calculate checksum
			String checksum = calculateChecksum(file.getBytes());

			// Check for duplicates
			Optional<Artifact> existing = artifactRepository.findByChecksum(checksum);
			if (existing.isPresent()) {
				throw new RuntimeException("Artifact with same content already exists");
			}

			// Store file
			String storageLocation = storageService.storeFile(file, dto.getInvestigationId());

			// Create artifact entity
			Artifact artifact = convertToEntity(dto);
			artifact.setFilePath(storageLocation);
			artifact.setFileSize(file.getSize());
			artifact.setContentType(file.getContentType());
			artifact.setChecksum(checksum);
			artifact.setStorageLocation(storageLocation);

			Artifact saved = artifactRepository.save(artifact);

			// Index for search
			searchService.indexArtifact(saved);

			publishArtifactEvent(saved, "ARTIFACT_UPLOADED");
			return convertToDto(saved);

		} catch (Exception e) {
			throw new RuntimeException("Failed to upload artifact: " + e.getMessage());
		}
	}

	@Cacheable(value = "artifacts", key = "#id")
	public Optional<ArtifactDto> getArtifactById(String id) {
		return artifactRepository.findById(id).map(this::convertToDto);
	}

	public Page<ArtifactDto> getAllArtifacts(Pageable pageable) {
		return artifactRepository.findByIsArchivedFalse(pageable).map(this::convertToDto);
	}

	public Page<ArtifactDto> searchArtifacts(String search, Pageable pageable) {
		try {
			List<String> artifactIds = searchService.searchArtifacts(search, pageable.getPageSize(),
					(int) pageable.getOffset());
			if (artifactIds.isEmpty()) {
				return Page.empty(pageable);
			}
			return artifactRepository.findByIdIn(artifactIds, pageable).map(this::convertToDto);
		} catch (Exception e) {
			// Fallback to database search
			return artifactRepository.searchArtifacts(search, pageable).map(this::convertToDto);
		}
	}

	public List<ArtifactDto> getArtifactsByType(ArtifactType type) {
		return artifactRepository.findByTypeAndIsArchivedFalse(type).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public List<ArtifactDto> getArtifactsByInvestigation(String investigationId) {
		return artifactRepository.findByInvestigationIdAndIsArchivedFalse(investigationId).stream()
				.map(this::convertToDto).collect(Collectors.toList());
	}

	public List<ArtifactDto> getArtifactsByCreator(String createdBy) {
		return artifactRepository.findByCreatedByAndIsArchivedFalse(createdBy).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public byte[] downloadArtifact(String id) {
		Optional<Artifact> artifactOpt = artifactRepository.findById(id);
		if (artifactOpt.isEmpty()) {
			throw new RuntimeException("Artifact not found with id: " + id);
		}

		Artifact artifact = artifactOpt.get();
		return storageService.retrieveFile(artifact.getStorageLocation());
	}

	@CacheEvict(value = "artifacts", key = "#id")
	@Transactional
	public ArtifactDto updateArtifact(String id, ArtifactDto dto) {
		Optional<Artifact> existingOpt = artifactRepository.findById(id);
		if (existingOpt.isEmpty()) {
			throw new RuntimeException("Artifact not found with id: " + id);
		}

		Artifact existing = existingOpt.get();
		existing.setName(dto.getName());
		existing.setDescription(dto.getDescription());
		existing.setMetadata(dto.getMetadata());

		Artifact updated = artifactRepository.save(existing);

		// Update search index
		searchService.indexArtifact(updated);

		publishArtifactEvent(updated, "ARTIFACT_UPDATED");
		return convertToDto(updated);
	}

	@CacheEvict(value = "artifacts", key = "#id")
	@Transactional
	public void archiveArtifact(String id) {
		Optional<Artifact> artifactOpt = artifactRepository.findById(id);
		if (artifactOpt.isEmpty()) {
			throw new RuntimeException("Artifact not found with id: " + id);
		}

		Artifact artifact = artifactOpt.get();
		artifact.archive();
		artifactRepository.save(artifact);

		// Remove from search index
		searchService.removeArtifactFromIndex(id);

		publishArtifactEvent(artifact, "ARTIFACT_ARCHIVED");
	}

	public List<Object[]> getArtifactTypeStatistics() {
		return artifactRepository.countByType();
	}

	public List<Object[]> getArtifactCreatorStatistics() {
		return artifactRepository.countByCreator();
	}

	public long getTotalArtifactCount() {
		return artifactRepository.countByIsArchivedFalse();
	}

	public long getTotalStorageSize() {
		return artifactRepository.sumFileSizeByIsArchivedFalse();
	}

	private String calculateChecksum(byte[] data) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(data);
			StringBuilder sb = new StringBuilder();
			for (byte b : hash) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException("Failed to calculate checksum: " + e.getMessage());
		}
	}

	private void publishArtifactEvent(Artifact artifact, String eventType) {
		try {
			ArtifactEvent event = new ArtifactEvent(eventType, convertToDto(artifact));
			kafkaTemplate.send("artifact-events", eventType, event);
		} catch (Exception e) {
			System.err.println("Failed to publish artifact event: " + e.getMessage());
		}
	}

	private Artifact convertToEntity(ArtifactDto dto) {
		Artifact entity = new Artifact();
		entity.setName(dto.getName());
		entity.setType(dto.getType());
		entity.setDescription(dto.getDescription());
		entity.setCreatedBy(dto.getCreatedBy());
		entity.setInvestigationId(dto.getInvestigationId());
		if (dto.getMetadata() != null) {
			entity.setMetadata(dto.getMetadata());
		}
		return entity;
	}

	private ArtifactDto convertToDto(Artifact entity) {
		ArtifactDto dto = new ArtifactDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setType(entity.getType());
		dto.setDescription(entity.getDescription());
		dto.setCreatedBy(entity.getCreatedBy());
		dto.setInvestigationId(entity.getInvestigationId());
		dto.setFileSize(entity.getFileSize());
		dto.setContentType(entity.getContentType());
		dto.setStorageLocation(entity.getStorageLocation());
		dto.setIsArchived(entity.getIsArchived());
		dto.setMetadata(entity.getMetadata());
		dto.setCreatedDate(entity.getCreatedDate());
		return dto;
	}

	// Event class
	public static class ArtifactEvent {
		private String eventType;
		private ArtifactDto data;
		private LocalDateTime timestamp;

		public ArtifactEvent(String eventType, ArtifactDto data) {
			this.eventType = eventType;
			this.data = data;
			this.timestamp = LocalDateTime.now();
		}

		// Getters and setters
		public String getEventType() {
			return eventType;
		}

		public void setEventType(String eventType) {
			this.eventType = eventType;
		}

		public ArtifactDto getData() {
			return data;
		}

		public void setData(ArtifactDto data) {
			this.data = data;
		}

		public LocalDateTime getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
		}
	}
}
