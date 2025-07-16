package com.ops.hunting.analytics.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ops.hunting.analytics.dto.DetectionAnalyticDto;
import com.ops.hunting.analytics.service.DetectionAnalyticService;
import com.ops.hunting.common.enums.Platform;
import com.ops.hunting.common.util.ResponseWrapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class DetectionAnalyticController {

	private final DetectionAnalyticService analyticService;

	@Autowired
	public DetectionAnalyticController(DetectionAnalyticService analyticService) {
		this.analyticService = analyticService;
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<DetectionAnalyticDto>> createAnalytic(
			@Valid @RequestBody DetectionAnalyticDto dto, Principal principal) {
		try {
			// Set creator from authenticated user
			dto.setCreatedBy(principal.getName());
			DetectionAnalyticDto created = analyticService.createAnalytic(dto);
			return ResponseEntity.ok(ResponseWrapper.success("Detection analytic created successfully", created));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to create detection analytic: " + e.getMessage()));
		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<DetectionAnalyticDto>> getAnalyticById(@PathVariable String id) {
		try {
			DetectionAnalyticDto dto = analyticService.getAnalyticById(id)
					.orElseThrow(() -> new RuntimeException("Detection analytic not found"));
			return ResponseEntity.ok(ResponseWrapper.success(dto));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get detection analytic: " + e.getMessage()));
		}
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Page<DetectionAnalyticDto>>> getAllAnalytics(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "createdDate") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDirection, @RequestParam(required = false) String search,
			@RequestParam(defaultValue = "false") boolean activeOnly) {

		try {
			Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending()
					: Sort.by(sortBy).ascending();
			Pageable pageable = PageRequest.of(page, size, sort);

			Page<DetectionAnalyticDto> result;
			if (search != null && !search.trim().isEmpty()) {
				result = analyticService.searchAnalytics(search, pageable);
			} else if (activeOnly) {
				result = analyticService.getActiveAnalytics(pageable);
			} else {
				result = analyticService.getAllAnalytics(pageable);
			}

			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get detection analytics: " + e.getMessage()));
		}
	}

	@GetMapping("/by-platform/{platform}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<DetectionAnalyticDto>>> getAnalyticsByPlatform(
			@PathVariable Platform platform) {
		try {
			List<DetectionAnalyticDto> result = analyticService.getAnalyticsByPlatform(platform);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get analytics by platform: " + e.getMessage()));
		}
	}

	@GetMapping("/my-analytics")
	@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<DetectionAnalyticDto>>> getMyAnalytics(Principal principal) {
		try {
			List<DetectionAnalyticDto> result = analyticService.getAnalyticsByCreator(principal.getName());
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get your analytics: " + e.getMessage()));
		}
	}

	@GetMapping("/by-threat-intelligence/{threatIntelligenceId}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<DetectionAnalyticDto>>> getAnalyticsByThreatIntelligence(
			@PathVariable String threatIntelligenceId) {
		try {
			List<DetectionAnalyticDto> result = analyticService.getAnalyticsByThreatIntelligence(threatIntelligenceId);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get analytics by threat intelligence: " + e.getMessage()));
		}
	}

	@GetMapping("/high-accuracy")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<DetectionAnalyticDto>>> getHighAccuracyAnalytics(
			@RequestParam(defaultValue = "80.0") BigDecimal minAccuracy) {
		try {
			List<DetectionAnalyticDto> result = analyticService.getHighAccuracyAnalytics(minAccuracy);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get high accuracy analytics: " + e.getMessage()));
		}
	}

	@GetMapping("/stale")
	@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<DetectionAnalyticDto>>> getStaleAnalytics(
			@RequestParam(defaultValue = "24") int hours) {
		try {
			List<DetectionAnalyticDto> result = analyticService.getStaleAnalytics(hours);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get stale analytics: " + e.getMessage()));
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<DetectionAnalyticDto>> updateAnalytic(@PathVariable String id,
			@Valid @RequestBody DetectionAnalyticDto dto) {
		try {
			DetectionAnalyticDto updated = analyticService.updateAnalytic(id, dto);
			return ResponseEntity.ok(ResponseWrapper.success("Detection analytic updated successfully", updated));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to update detection analytic: " + e.getMessage()));
		}
	}

	@PostMapping("/{id}/activate")
	@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<DetectionAnalyticDto>> activateAnalytic(@PathVariable String id) {
		try {
			DetectionAnalyticDto activated = analyticService.activateAnalytic(id);
			return ResponseEntity.ok(ResponseWrapper.success("Detection analytic activated successfully", activated));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to activate detection analytic: " + e.getMessage()));
		}
	}

	@PostMapping("/{id}/deactivate")
	@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<DetectionAnalyticDto>> deactivateAnalytic(@PathVariable String id) {
		try {
			DetectionAnalyticDto deactivated = analyticService.deactivateAnalytic(id);
			return ResponseEntity
					.ok(ResponseWrapper.success("Detection analytic deactivated successfully", deactivated));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to deactivate detection analytic: " + e.getMessage()));
		}
	}

	@PostMapping("/{id}/execute")
	@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<String>> executeAnalytic(@PathVariable UUID id) {
		try {
			analyticService.executeAnalytic(id);
			return ResponseEntity.ok(ResponseWrapper.success("Detection analytic executed successfully"));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to execute detection analytic: " + e.getMessage()));
		}
	}

	@PostMapping("/execute-all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ResponseWrapper<String>> executeAllActiveAnalytics() {
		try {
			analyticService.executeAllActiveAnalytics();
			return ResponseEntity.ok(ResponseWrapper.success("All active analytics executed successfully"));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to execute all analytics: " + e.getMessage()));
		}
	}

	@GetMapping("/statistics/platform")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Map<String, Long>>> getPlatformStatistics() {
		try {
			List<Object[]> stats = analyticService.getPlatformStatistics();
			Map<String, Long> result = stats.stream()
					.collect(Collectors.toMap(arr -> ((Platform) arr[0]).name(), arr -> (Long) arr[1]));
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get platform statistics: " + e.getMessage()));
		}
	}

	@GetMapping("/statistics/creator")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ResponseWrapper<Map<String, Long>>> getCreatorStatistics() {
		try {
			List<Object[]> stats = analyticService.getCreatorStatistics();
			Map<String, Long> result = stats.stream()
					.collect(Collectors.toMap(arr -> (String) arr[0], arr -> (Long) arr[1]));
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get creator statistics: " + e.getMessage()));
		}
	}

	@GetMapping("/statistics/summary")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Map<String, Object>>> getSummaryStatistics() {
		try {
			long activeCount = analyticService.getActiveAnalyticsCount();
			BigDecimal avgAccuracy = analyticService.getAverageAccuracy();

			Map<String, Object> summary = Map.of("activeAnalytics", activeCount, "averageAccuracy",
					avgAccuracy != null ? avgAccuracy : BigDecimal.ZERO);

			return ResponseEntity.ok(ResponseWrapper.success(summary));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get summary statistics: " + e.getMessage()));
		}
	}

	@GetMapping("/health")
	public ResponseEntity<ResponseWrapper<String>> health() {
		return ResponseEntity.ok(ResponseWrapper.success("Detection Analytics Service is healthy"));
	}
}
