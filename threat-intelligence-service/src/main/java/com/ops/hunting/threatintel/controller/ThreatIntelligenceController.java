package com.ops.hunting.threatintel.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.ops.hunting.common.enums.SeverityLevel;
import com.ops.hunting.common.enums.ThreatType;
import com.ops.hunting.common.util.ResponseWrapper;
import com.ops.hunting.threatintel.dto.ThreatIntelligenceDto;
import com.ops.hunting.threatintel.service.ThreatIntelligenceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/threat-intelligence")
@CrossOrigin(origins = "*")
public class ThreatIntelligenceController {

	private final ThreatIntelligenceService threatIntelligenceService;

	@Autowired
	public ThreatIntelligenceController(ThreatIntelligenceService threatIntelligenceService) {
		this.threatIntelligenceService = threatIntelligenceService;
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<ThreatIntelligenceDto>> createThreatIntelligence(
			@Valid @RequestBody ThreatIntelligenceDto dto) {
		try {
			ThreatIntelligenceDto created = threatIntelligenceService.createThreatIntelligence(dto);
			return ResponseEntity.ok(ResponseWrapper.success("Threat intelligence created successfully", created));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to create threat intelligence: " + e.getMessage()));
		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<ThreatIntelligenceDto>> getThreatIntelligenceById(@PathVariable String id) {
		try {
			ThreatIntelligenceDto dto = threatIntelligenceService.getThreatIntelligenceById(id)
					.orElseThrow(() -> new RuntimeException("Threat intelligence not found"));
			return ResponseEntity.ok(ResponseWrapper.success(dto));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get threat intelligence: " + e.getMessage()));
		}
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Page<ThreatIntelligenceDto>>> getAllThreatIntelligence(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "discoveredDate") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDirection, @RequestParam(required = false) String search) {

		try {
			Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending()
					: Sort.by(sortBy).ascending();
			Pageable pageable = PageRequest.of(page, size, sort);

			Page<ThreatIntelligenceDto> result = search != null && !search.trim().isEmpty()
					? threatIntelligenceService.searchThreatIntelligence(search, pageable)
					: threatIntelligenceService.getAllThreatIntelligence(pageable);

			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get threat intelligence: " + e.getMessage()));
		}
	}

	@GetMapping("/by-type/{threatType}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<ThreatIntelligenceDto>>> getThreatIntelligenceByType(
			@PathVariable ThreatType threatType) {
		try {
			List<ThreatIntelligenceDto> result = threatIntelligenceService.getThreatIntelligenceByType(threatType);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get threat intelligence by type: " + e.getMessage()));
		}
	}

	@GetMapping("/by-severity/{severity}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<ThreatIntelligenceDto>>> getThreatIntelligenceBySeverity(
			@PathVariable SeverityLevel severity) {
		try {
			List<ThreatIntelligenceDto> result = threatIntelligenceService.getThreatIntelligenceBySeverity(severity);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get threat intelligence by severity: " + e.getMessage()));
		}
	}

	@GetMapping("/by-source/{source}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<ThreatIntelligenceDto>>> getThreatIntelligenceBySource(
			@PathVariable String source) {
		try {
			List<ThreatIntelligenceDto> result = threatIntelligenceService.getThreatIntelligenceBySource(source);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get threat intelligence by source: " + e.getMessage()));
		}
	}

	@GetMapping("/by-date-range")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<ThreatIntelligenceDto>>> getThreatIntelligenceByDateRange(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
		try {
			List<ThreatIntelligenceDto> result = threatIntelligenceService.getThreatIntelligenceByDateRange(startDate,
					endDate);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get threat intelligence by date range: " + e.getMessage()));
		}
	}

	@GetMapping("/high-priority")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<ThreatIntelligenceDto>>> getHighPriorityThreatIntelligence() {
		try {
			List<ThreatIntelligenceDto> result = threatIntelligenceService.getHighPriorityThreatIntelligence();
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get high priority threat intelligence: " + e.getMessage()));
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<ThreatIntelligenceDto>> updateThreatIntelligence(@PathVariable String id,
			@Valid @RequestBody ThreatIntelligenceDto dto) {
		try {
			ThreatIntelligenceDto updated = threatIntelligenceService.updateThreatIntelligence(id, dto);
			return ResponseEntity.ok(ResponseWrapper.success("Threat intelligence updated successfully", updated));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to update threat intelligence: " + e.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ResponseWrapper<Void>> deleteThreatIntelligence(@PathVariable String id) {
		try {
			threatIntelligenceService.deleteThreatIntelligence(id);
			return ResponseEntity.ok(ResponseWrapper.success("Threat intelligence deleted successfully", null));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to delete threat intelligence: " + e.getMessage()));
		}
	}

	@GetMapping("/statistics/recent")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Map<String, Object>>> getRecentStatistics(
			@RequestParam(defaultValue = "30") int days) {
		try {
			long recentCount = threatIntelligenceService.getRecentThreatIntelligenceCount(days);
			Map<String, Object> stats = Map.of("recentCount", recentCount, "days", days);
			return ResponseEntity.ok(ResponseWrapper.success(stats));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get recent statistics: " + e.getMessage()));
		}
	}

	@GetMapping("/statistics/threat-types")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Map<String, Long>>> getThreatTypeStatistics() {
		try {
			List<Object[]> stats = threatIntelligenceService.getThreatTypeStatistics();
			Map<String, Long> result = stats.stream()
					.collect(Collectors.toMap(arr -> ((ThreatType) arr[0]).name(), arr -> (Long) arr[1]));
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get threat type statistics: " + e.getMessage()));
		}
	}

	@GetMapping("/statistics/severity")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Map<String, Long>>> getSeverityStatistics() {
		try {
			List<Object[]> stats = threatIntelligenceService.getSeverityStatistics();
			Map<String, Long> result = stats.stream()
					.collect(Collectors.toMap(arr -> ((SeverityLevel) arr[0]).name(), arr -> (Long) arr[1]));
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get severity statistics: " + e.getMessage()));
		}
	}

	@GetMapping("/health")
	public ResponseEntity<ResponseWrapper<String>> health() {
		return ResponseEntity.ok(ResponseWrapper.success("Threat Intelligence Service is healthy"));
	}
}
