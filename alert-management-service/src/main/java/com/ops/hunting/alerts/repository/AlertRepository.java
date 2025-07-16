package com.ops.hunting.alerts.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ops.hunting.alerts.entity.Alert;
import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.enums.AlertStatus;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {

	List<Alert> findByStatus(AlertStatus status);

	List<Alert> findBySeverity(AlertSeverity severity);

	List<Alert> findByStatusAndSeverity(AlertStatus status, AlertSeverity severity);

	List<Alert> findBySourceSystem(String sourceSystem);

	List<Alert> findByAssignedTo(String assignedTo);

	List<Alert> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	Page<Alert> findBySeverityAndStatusAndCreatedAtBetween(AlertSeverity severity, AlertStatus status,
			LocalDateTime start, LocalDateTime end, Pageable pageable);

	long countByStatus(AlertStatus status);

	long countBySeverity(AlertSeverity severity);

	long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	@Query("SELECT a FROM Alert a WHERE a.severity = :severity AND a.createdAt >= :since")
	List<Alert> findHighSeverityAlertsSince(@Param("severity") AlertSeverity severity,
			@Param("since") LocalDateTime since);

	@Query("SELECT a FROM Alert a WHERE a.status = 'OPEN' AND a.createdAt < :threshold")
	List<Alert> findStaleOpenAlerts(@Param("threshold") LocalDateTime threshold);

	@Query("SELECT DISTINCT a.sourceSystem FROM Alert a")
	List<String> findDistinctSourceSystems();

	@Query("SELECT AVG(EXTRACT(EPOCH FROM (a.closedAt - a.createdAt))) FROM Alert a WHERE a.status = 'CLOSED' AND a.closedAt IS NOT NULL")
	Double calculateAverageResolutionTimeInSeconds();
}