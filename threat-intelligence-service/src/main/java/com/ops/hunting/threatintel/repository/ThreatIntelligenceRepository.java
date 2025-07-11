package com.ops.hunting.threatintel.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ops.hunting.common.enums.SeverityLevel;
import com.ops.hunting.common.enums.ThreatType;
import com.ops.hunting.threatintel.entity.ThreatIntelligence;

@Repository
public interface ThreatIntelligenceRepository extends JpaRepository<ThreatIntelligence, String> {

	List<ThreatIntelligence> findByThreatType(ThreatType threatType);

	List<ThreatIntelligence> findBySeverity(SeverityLevel severity);

	List<ThreatIntelligence> findBySource(String source);

	Page<ThreatIntelligence> findByThreatType(ThreatType threatType, Pageable pageable);

	Page<ThreatIntelligence> findBySeverity(SeverityLevel severity, Pageable pageable);

	@Query("SELECT ti FROM ThreatIntelligence ti WHERE ti.discoveredDate >= :startDate AND ti.discoveredDate <= :endDate")
	List<ThreatIntelligence> findByDiscoveredDateBetween(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("SELECT ti FROM ThreatIntelligence ti WHERE ti.severity IN :severities")
	List<ThreatIntelligence> findBySeverityIn(@Param("severities") List<SeverityLevel> severities);

	@Query("SELECT ti FROM ThreatIntelligence ti WHERE "
			+ "LOWER(ti.description) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "LOWER(ti.ttp) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "LOWER(ti.source) LIKE LOWER(CONCAT('%', :search, '%'))")
	Page<ThreatIntelligence> searchThreatIntelligence(@Param("search") String search, Pageable pageable);

	@Query("SELECT COUNT(ti) FROM ThreatIntelligence ti WHERE ti.discoveredDate >= :date")
	long countByDiscoveredDateAfter(@Param("date") LocalDateTime date);

	@Query("SELECT ti.threatType, COUNT(ti) FROM ThreatIntelligence ti GROUP BY ti.threatType")
	List<Object[]> countByThreatType();

	@Query("SELECT ti.severity, COUNT(ti) FROM ThreatIntelligence ti GROUP BY ti.severity")
	List<Object[]> countBySeverity();
}
