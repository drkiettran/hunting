package com.ops.hunting.alerts.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ops.hunting.alerts.entity.Alert;
import com.ops.hunting.common.enums.AlertStatus;
import com.ops.hunting.common.enums.SeverityLevel;

@Repository
public interface AlertRepository extends JpaRepository<Alert, String> {

	List<Alert> findByStatus(AlertStatus status);

	List<Alert> findBySeverity(SeverityLevel severity);

	List<Alert> findByAssignedTo(String assignedTo);

	Page<Alert> findByStatus(AlertStatus status, Pageable pageable);

	Page<Alert> findBySeverity(SeverityLevel severity, Pageable pageable);

	Page<Alert> findByAssignedTo(String assignedTo, Pageable pageable);

	@Query("SELECT a FROM Alert a WHERE a.timestamp >= :startDate AND a.timestamp <= :endDate")
	List<Alert> findByTimestampBetween(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("SELECT a FROM Alert a WHERE a.severity IN :severities AND a.status IN :statuses")
	List<Alert> findBySeverityInAndStatusIn(@Param("severities") List<SeverityLevel> severities,
			@Param("statuses") List<AlertStatus> statuses);

	@Query("SELECT a FROM Alert a WHERE a.falsePositive = false AND a.status != 'FALSE_POSITIVE'")
	Page<Alert> findActiveAlerts(Pageable pageable);

	@Query("SELECT a FROM Alert a WHERE " + "LOWER(a.description) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "LOWER(a.rawData) LIKE LOWER(CONCAT('%', :search, '%'))")
	Page<Alert> searchAlerts(@Param("search") String search, Pageable pageable);

	@Query("SELECT COUNT(a) FROM Alert a WHERE a.status = :status")
	long countByStatus(@Param("status") AlertStatus status);

	@Query("SELECT COUNT(a) FROM Alert a WHERE a.severity = :severity AND a.timestamp >= :since")
	long countBySeverityAndTimestampAfter(@Param("severity") SeverityLevel severity,
			@Param("since") LocalDateTime since);

	@Query("SELECT a.status, COUNT(a) FROM Alert a GROUP BY a.status")
	List<Object[]> countByStatus();

	@Query("SELECT a.severity, COUNT(a) FROM Alert a GROUP BY a.severity")
	List<Object[]> countBySeverity();

	@Query("SELECT DATE(a.timestamp), COUNT(a) FROM Alert a WHERE a.timestamp >= :since GROUP BY DATE(a.timestamp)")
	List<Object[]> countDailyAlerts(@Param("since") LocalDateTime since);

	@Query("SELECT a.assignedTo, COUNT(a) FROM Alert a WHERE a.assignedTo IS NOT NULL GROUP BY a.assignedTo")
	List<Object[]> countByAssignedAnalyst();
}
