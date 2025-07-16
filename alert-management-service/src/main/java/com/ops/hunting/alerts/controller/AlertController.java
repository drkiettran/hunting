package com.ops.hunting.alerts.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ops.hunting.alerts.dto.AlertDTO;
import com.ops.hunting.alerts.dto.AlertSummaryDTO;
import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.enums.AlertStatus;
import com.ops.hunting.alerts.service.AlertService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Alert Management", description = "API for managing security alerts")
public class AlertController {

	private final AlertService alertService;

	@PostMapping
	@Operation(summary = "Create a new alert")
	@PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
	public ResponseEntity<AlertDTO> createAlert(@Valid @RequestBody AlertDTO alertDTO) {
		log.info("Creating new alert: {}", alertDTO.getTitle());
		AlertDTO createdAlert = alertService.createAlert(alertDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdAlert);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get alert by ID")
	@PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
	public ResponseEntity<AlertDTO> getAlertById(@PathVariable UUID id) {
		log.debug("Fetching alert with ID: {}", id);
		AlertDTO alert = alertService.getAlertById(id);
		return ResponseEntity.ok(alert);
	}

	@GetMapping
	@Operation(summary = "Get all alerts with pagination")
	@PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
	public ResponseEntity<Page<AlertDTO>> getAllAlerts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

		Pageable pageable = PageRequest.of(page, size, sort);
		Page<AlertDTO> alerts = alertService.getAllAlerts(pageable);
		return ResponseEntity.ok(alerts);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update alert")
	@PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
	public ResponseEntity<AlertDTO> updateAlert(@PathVariable UUID id, @Valid @RequestBody AlertDTO alertDTO) {
		log.info("Updating alert with ID: {}", id);
		AlertDTO updatedAlert = alertService.updateAlert(id, alertDTO);
		return ResponseEntity.ok(updatedAlert);
	}

	@PatchMapping("/{id}/status")
	@Operation(summary = "Update alert status")
	@PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
	public ResponseEntity<AlertDTO> updateAlertStatus(@PathVariable UUID id, @RequestParam AlertStatus status,
			@RequestParam(required = false) String assignedTo) {

		log.info("Updating alert status for ID: {} to {}", id, status);
		AlertDTO updatedAlert = alertService.updateAlertStatus(id, status, assignedTo);
		return ResponseEntity.ok(updatedAlert);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete alert")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteAlert(@PathVariable UUID id) {
		log.info("Deleting alert with ID: {}", id);
		alertService.deleteAlert(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/search")
	@Operation(summary = "Search alerts with filters")
	@PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
	public ResponseEntity<Page<AlertDTO>> searchAlerts(@RequestParam(required = false) AlertSeverity severity,
			@RequestParam(required = false) AlertStatus status,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<AlertDTO> alerts = alertService.searchAlerts(severity, status, from, to, pageable);
		return ResponseEntity.ok(alerts);
	}

	@GetMapping("/summary")
	@Operation(summary = "Get alert summary statistics")
	@PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
	public ResponseEntity<AlertSummaryDTO> getAlertSummary() {
		log.debug("Generating alert summary");
		AlertSummaryDTO summary = alertService.getAlertSummary();
		return ResponseEntity.ok(summary);
	}

	@GetMapping("/assigned/{assignee}")
	@Operation(summary = "Get alerts assigned to a specific user")
	@PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
	public ResponseEntity<List<AlertDTO>> getAlertsByAssignee(@PathVariable String assignee) {
		log.debug("Fetching alerts for assignee: {}", assignee);
		List<AlertDTO> alerts = alertService.getAlertsByAssignee(assignee);
		return ResponseEntity.ok(alerts);
	}

	@GetMapping("/stale")
	@Operation(summary = "Get stale alerts")
	@PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
	public ResponseEntity<List<AlertDTO>> getStaleAlerts(@RequestParam(defaultValue = "24") int hoursThreshold) {
		log.debug("Fetching stale alerts older than {} hours", hoursThreshold);
		List<AlertDTO> staleAlerts = alertService.getStaleAlerts(hoursThreshold);
		return ResponseEntity.ok(staleAlerts);
	}

	@PatchMapping("/bulk-status")
	@Operation(summary = "Bulk update alert status")
	@PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
	public ResponseEntity<Void> bulkUpdateStatus(@RequestParam List<UUID> alertIds, @RequestParam AlertStatus status,
			@RequestParam String updatedBy) {

		log.info("Bulk updating {} alerts to status: {}", alertIds.size(), status);
		alertService.bulkUpdateStatus(alertIds, status, updatedBy);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/source-systems")
	@Operation(summary = "Get distinct source systems")
	@PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('ADMIN')")
	public ResponseEntity<List<String>> getSourceSystems() {
		log.debug("Fetching distinct source systems");
		List<String> sourceSystems = alertService.getSourceSystems();
		return ResponseEntity.ok(sourceSystems);
	}
}