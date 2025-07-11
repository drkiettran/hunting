package com.ops.hunting.knowedge.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ops.hunting.common.enums.ArtifactType;
import com.ops.hunting.common.util.ResponseWrapper;
import com.ops.hunting.knowledge.dto.ArtifactDto;
import com.ops.hunting.knowledge.service.ArtifactService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/artifacts")
@CrossOrigin(origins = "*")
public class ArtifactController {

	private final ArtifactService artifactService;

	@Autowired
	public ArtifactController(ArtifactService artifactService) {
		this.artifactService = artifactService;
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<ArtifactDto>> uploadArtifact(@RequestParam("file") MultipartFile file,
			@RequestParam("name") String name, @RequestParam("type") ArtifactType type,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "investigationId", required = false) String investigationId, Principal principal) {
		try {
			ArtifactDto dto = new ArtifactDto();
			dto.setName(name);
			dto.setType(type);
			dto.setDescription(description);
			dto.setCreatedBy(principal.getName());
			dto.setInvestigationId(investigationId);

			ArtifactDto uploaded = artifactService.uploadArtifact(file, dto);
			return ResponseEntity.ok(ResponseWrapper.success("Artifact uploaded successfully", uploaded));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to upload artifact: " + e.getMessage()));
		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<ArtifactDto>> getArtifactById(@PathVariable String id) {
		try {
			ArtifactDto dto = artifactService.getArtifactById(id)
					.orElseThrow(() -> new RuntimeException("Artifact not found"));
			return ResponseEntity.ok(ResponseWrapper.success(dto));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseWrapper.error("Failed to get artifact: " + e.getMessage()));
		}
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Page<ArtifactDto>>> getAllArtifacts(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "createdDate") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDirection, @RequestParam(required = false) String search) {

		try {
			Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending()
					: Sort.by(sortBy).ascending();
			Pageable pageable = PageRequest.of(page, size, sort);

			Page<ArtifactDto> result = search != null && !search.trim().isEmpty()
					? artifactService.searchArtifacts(search, pageable)
					: artifactService.getAllArtifacts(pageable);

			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get artifacts: " + e.getMessage()));
		}
	}

	@GetMapping("/by-type/{type}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<ArtifactDto>>> getArtifactsByType(@PathVariable ArtifactType type) {
		try {
			List<ArtifactDto> result = artifactService.getArtifactsByType(type);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get artifacts by type: " + e.getMessage()));
		}
	}

	@GetMapping("/by-investigation/{investigationId}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<ArtifactDto>>> getArtifactsByInvestigation(
			@PathVariable String investigationId) {
		try {
			List<ArtifactDto> result = artifactService.getArtifactsByInvestigation(investigationId);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get artifacts by investigation: " + e.getMessage()));
		}
	}

	@GetMapping("/my-artifacts")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<ArtifactDto>>> getMyArtifacts(Principal principal) {
		try {
			List<ArtifactDto> result = artifactService.getArtifactsByCreator(principal.getName());
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get your artifacts: " + e.getMessage()));
		}
	}

	@GetMapping("/{id}/download")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<byte[]> downloadArtifact(@PathVariable String id) {
		try {
			ArtifactDto artifact = artifactService.getArtifactById(id)
					.orElseThrow(() -> new RuntimeException("Artifact not found"));

			byte[] fileContent = artifactService.downloadArtifact(id);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType(artifact.getContentType()));
			headers.setContentDispositionFormData("attachment", artifact.getName());

			return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<ArtifactDto>> updateArtifact(@PathVariable String id,
			@Valid @RequestBody ArtifactDto dto) {
		try {
			ArtifactDto updated = artifactService.updateArtifact(id, dto);
			return ResponseEntity.ok(ResponseWrapper.success("Artifact updated successfully", updated));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to update artifact: " + e.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Void>> archiveArtifact(@PathVariable String id) {
		try {
			artifactService.archiveArtifact(id);
			return ResponseEntity.ok(ResponseWrapper.success("Artifact archived successfully", null));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to archive artifact: " + e.getMessage()));
		}
	}

	@GetMapping("/statistics/type")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Map<String, Long>>> getTypeStatistics() {
		try {
			List<Object[]> stats = artifactService.getArtifactTypeStatistics();
			Map<String, Long> result = stats.stream()
					.collect(Collectors.toMap(arr -> ((ArtifactType) arr[0]).name(), arr -> (Long) arr[1]));
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get type statistics: " + e.getMessage()));
		}
	}

	@GetMapping("/statistics/summary")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Map<String, Object>>> getSummaryStatistics() {
		try {
			long totalCount = artifactService.getTotalArtifactCount();
			long totalSize = artifactService.getTotalStorageSize();

			Map<String, Object> summary = Map.of("totalArtifacts", totalCount, "totalStorageSize", totalSize,
					"totalStorageSizeMB", totalSize / (1024 * 1024));

			return ResponseEntity.ok(ResponseWrapper.success(summary));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get summary statistics: " + e.getMessage()));
		}
	}

	@GetMapping("/health")
	public ResponseEntity<ResponseWrapper<String>> health() {
		return ResponseEntity.ok(ResponseWrapper.success("Knowledge Base Service is healthy"));
	}
}