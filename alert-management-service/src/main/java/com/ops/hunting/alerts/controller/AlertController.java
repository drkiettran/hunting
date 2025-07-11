package com.ops.hunting.alerts.controller;

import java.security.Principal;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ops.hunting.alerts.dto.AlertCreateDto;
import com.ops.hunting.alerts.dto.AlertUpdateDto;
import com.ops.hunting.alerts.service.AlertService;
import com.ops.hunting.common.dto.AlertDto;
import com.ops.hunting.common.enums.AlertStatus;
import com.ops.hunting.common.enums.SeverityLevel;
import com.ops.hunting.common.util.ResponseWrapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

	private final AlertService alertService;

	@Autowired
	public AlertController(AlertService alertService) {
		this.alertService = alertService;
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<AlertDto>> createAlert(@Valid @RequestBody AlertCreateDto createDto) {
		try {
			AlertDto created = alertService.createAlert(createDto);
			return ResponseEntity.ok(ResponseWrapper.success("Alert created successfully", created));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseWrapper.error("Failed to create alert: " + e.getMessage()));
		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<AlertDto>> getAlertById(@PathVariable String id) {
		try {
			AlertDto dto = alertService.getAlertById(id).orElseThrow(() -> new RuntimeException("Alert not found"));
			return ResponseEntity.ok(ResponseWrapper.success(dto));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseWrapper.error("Failed to get alert: " + e.getMessage()));
		}
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Page<AlertDto>>> getAllAlerts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "timestamp") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDirection, @RequestParam(required = false) String search,
			@RequestParam(defaultValue = "false") boolean activeOnly) {

		try {
			Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending()
					: Sort.by(sortBy).ascending();
			Pageable pageable = PageRequest.of(page, size, sort);

			Page<AlertDto> result;
			if (search != null && !search.trim().isEmpty()) {
				result = alertService.searchAlerts(search, pageable);
			} else if (activeOnly) {
				result = alertService.getActiveAlerts(pageable);
			} else {
				result = alertService.getAllAlerts(pageable);
			}

			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseWrapper.error("Failed to get alerts: " + e.getMessage()));
		}
	}

	@GetMapping("/by-status/{status}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Page<AlertDto>>> getAlertsByStatus(@PathVariable AlertStatus status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
			Page<AlertDto> result = alertService.getAlertsByStatus(status, pageable);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get alerts by status: " + e.getMessage()));
		}
	}

	@GetMapping("/by-severity/{severity}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Page<AlertDto>>> getAlertsBySeverity(@PathVariable SeverityLevel severity,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
			Page<AlertDto> result = alertService.getAlertsBySeverity(severity, pageable);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get alerts by severity: " + e.getMessage()));
		}
	}

	@GetMapping("/assigned-to-me")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Page<AlertDto>>> getMyAssignedAlerts(Principal principal,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
			Page<AlertDto> result = alertService.getAlertsByAssignedTo(principal.getName(), pageable);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get assigned alerts: " + e.getMessage()));
		}
	}

	@GetMapping("/by-date-range")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<AlertDto>>> getAlertsByDateRange(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
		try {
			List<AlertDto> result = alertService.getAlertsByDateRange(startDate, endDate);
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get alerts by date range: " + e.getMessage()));
		}
	}

	@GetMapping("/high-priority")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<AlertDto>>> getHighPriorityAlerts() {
		try {
			List<AlertDto> result = alertService.getHighPriorityAlerts();
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get high priority alerts: " + e.getMessage()));
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<AlertDto>> updateAlert(@PathVariable String id,
			@Valid @RequestBody AlertUpdateDto updateDto) {
		try {
			AlertDto updated = alertService.updateAlert(id, updateDto);
			return ResponseEntity.ok(ResponseWrapper.success("Alert updated successfully", updated));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseWrapper.error("Failed to update alert: " + e.getMessage()));
		}
	}

	@PostMapping("/{id}/assign")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<AlertDto>> assignAlert(@PathVariable String id,
			@RequestBody Map<String, String> request) {
		try {
			String analystId = request.get("analystId");
			if (analystId == null || analystId.trim().isEmpty()) {
				throw new RuntimeException("Analyst ID is required");
			}
			AlertDto assigned = alertService.assignAlert(id, analystId);
			return ResponseEntity.ok(ResponseWrapper.success("Alert assigned successfully", assigned));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseWrapper.error("Failed to assign alert: " + e.getMessage()));
		}
	}

	@PostMapping("/{id}/assign-to-me")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<AlertDto>> assignAlertToMe(@PathVariable String id, Principal principal) {
		try {
			AlertDto assigned = alertService.assignAlert(id, principal.getName());
			return ResponseEntity.ok(ResponseWrapper.success("Alert assigned to you successfully", assigned));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseWrapper.error("Failed to assign alert: " + e.getMessage()));
		}
	}

	@PostMapping("/{id}/in-progress")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<AlertDto>> markInProgress(@PathVariable String id) {
		try {
			AlertDto updated = alertService.markInProgress(id);
			return ResponseEntity.ok(ResponseWrapper.success("Alert marked as in progress", updated));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to mark alert as in progress: " + e.getMessage()));
		}
	}

	@PostMapping("/{id}/resolve")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<AlertDto>> resolveAlert(@PathVariable String id,
			@RequestBody Map<String, String> request) {
		try {
			String notes = request.get("resolutionNotes");
			AlertDto resolved = alertService.resolveAlert(id, notes);
			return ResponseEntity.ok(ResponseWrapper.success("Alert resolved successfully", resolved));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to resolve alert: " + e.getMessage()));
		}
	}

	@PostMapping("/{id}/false-positive")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<AlertDto>> markAsFalsePositive(@PathVariable String id,
			@RequestBody Map<String, String> request) {
		try {
			String notes = request.get("notes");
			AlertDto updated = alertService.markAsFalsePositive(id, notes);
			return ResponseEntity.ok(ResponseWrapper.success("Alert marked as false positive", updated));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to mark alert as false positive: " + e.getMessage()));
		}
	}

	@GetMapping("/statistics/status")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Map<String, Long>>> getStatusStatistics() {
		try {
			List<Object[]> stats = alertService.getAlertStatusStatistics();
			Map<String, Long> result = stats.stream()
					.collect(Collectors.toMap(arr -> ((AlertStatus) arr[0]).name(), arr -> (Long) arr[1]));
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get status statistics: " + e.getMessage()));
		}
	}

	@GetMapping("/statistics/severity")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<Map<String, Long>>> getSeverityStatistics() {
		try {
			List<Object[]> stats = alertService.getAlertSeverityStatistics();
			Map<String, Long> result = stats.stream()
					.collect(Collectors.toMap(arr -> ((SeverityLevel) arr[0]).name(), arr -> (Long) arr[1]));
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get severity statistics: " + e.getMessage()));
		}
	}

	@GetMapping("/statistics/daily")
	@PreAuthorize("hasAnyRole('ANALYST', 'PRODUCTION_STAFF', 'ADMIN')")
	public ResponseEntity<ResponseWrapper<List<Map<String, Object>>>> getDailyStatistics(
			@RequestParam(defaultValue = "30") int days) {
		try {
			List<Object[]> stats = alertService.getDailyAlertStatistics(days);
			List<Map<String, Object>> result = stats.stream()
					.map(arr -> Map.of("date", arr[0].toString(), "count", arr[1])).collect(Collectors.toList());
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get daily statistics: " + e.getMessage()));
		}
	}

	@GetMapping("/statistics/workload")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ResponseWrapper<Map<String, Long>>> getWorkloadStatistics() {
		try {
			List<Object[]> stats = alertService.getAnalystWorkloadStatistics();
			Map<String, Long> result = stats.stream()
					.collect(Collectors.toMap(arr -> (String) arr[0], arr -> (Long) arr[1]));
			return ResponseEntity.ok(ResponseWrapper.success(result));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(ResponseWrapper.error("Failed to get workload statistics: " + e.getMessage()));
		}
	}

	@GetMapping("/health")
	public ResponseEntity<ResponseWrapper<String>> health() {
		return ResponseEntity.ok(ResponseWrapper.success("Alert Management Service is healthy"));
	}
}
