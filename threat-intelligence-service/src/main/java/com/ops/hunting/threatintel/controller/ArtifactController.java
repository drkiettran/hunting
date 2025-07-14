package com.ops.hunting.threatintel.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.ops.hunting.threatintel.dto.ArtifactRequest;
import com.ops.hunting.threatintel.dto.ArtifactResponse;
import com.ops.hunting.threatintel.entity.ArtifactType;
import com.ops.hunting.threatintel.service.ArtifactService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/artifacts")
@CrossOrigin(origins = "*")
public class ArtifactController {

	@Autowired
	private ArtifactService artifactService;

	@PostMapping
	public ResponseEntity<ArtifactResponse> createArtifact(@Valid @RequestBody ArtifactRequest request) {
		try {
			ArtifactResponse response = artifactService.createArtifact(request);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{artifactId}")
	public ResponseEntity<ArtifactResponse> getArtifact(@PathVariable String artifactId) {
		Optional<ArtifactResponse> artifact = artifactService.getArtifactById(artifactId);
		return artifact.map(response -> ResponseEntity.ok().body(response)).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping
	public ResponseEntity<Page<ArtifactResponse>> getAllArtifacts(Pageable pageable) {
		Page<ArtifactResponse> artifacts = artifactService.getAllArtifacts(pageable);
		return ResponseEntity.ok(artifacts);
	}

	@GetMapping("/type/{type}")
	public ResponseEntity<List<ArtifactResponse>> getArtifactsByType(@PathVariable ArtifactType type) {
		List<ArtifactResponse> artifacts = artifactService.getArtifactsByType(type);
		return ResponseEntity.ok(artifacts);
	}

	@PutMapping("/{artifactId}")
	public ResponseEntity<ArtifactResponse> updateArtifact(@PathVariable String artifactId,
			@Valid @RequestBody ArtifactRequest request) {
		try {
			ArtifactResponse response = artifactService.updateArtifact(artifactId, request);
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/{artifactId}")
	public ResponseEntity<Void> deleteArtifact(@PathVariable String artifactId) {
		try {
			artifactService.deleteArtifact(artifactId);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/search")
	public ResponseEntity<List<ArtifactResponse>> searchArtifacts(@RequestParam String query) {
		List<ArtifactResponse> artifacts = artifactService.searchArtifacts(query);
		return ResponseEntity.ok(artifacts);
	}
}