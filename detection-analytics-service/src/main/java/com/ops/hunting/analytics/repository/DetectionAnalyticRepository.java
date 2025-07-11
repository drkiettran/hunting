package com.ops.hunting.analytics.repository;

import com.ops.hunting.analytics.entity.DetectionAnalytic;
import com.ops.hunting.common.enums.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetectionAnalyticRepository extends JpaRepository<DetectionAnalytic, String> {

	List<DetectionAnalytic> findByPlatform(Platform platform);

	List<DetectionAnalytic> findByCreatedBy(String createdBy);

	List<DetectionAnalytic> findByIsActiveTrue();

	Page<DetectionAnalytic> findByPlatform(Platform platform, Pageable pageable);

	Page<DetectionAnalytic> findByIsActiveTrue(Pageable pageable);

	Page<DetectionAnalytic> findByCreatedBy(String createdBy, Pageable pageable);

	@Query("SELECT da FROM DetectionAnalytic da WHERE da.threatIntelligenceId = :threatIntelligenceId")
	List<DetectionAnalytic> findByThreatIntelligenceId(@Param("threatIntelligenceId") String threatIntelligenceId);

	@Query("SELECT da FROM DetectionAnalytic da WHERE da.accuracy >= :minAccuracy")
	List<DetectionAnalytic> findByAccuracyGreaterThanEqual(@Param("minAccuracy") BigDecimal minAccuracy);

	@Query("SELECT da FROM DetectionAnalytic da WHERE " + "LOWER(da.name) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "LOWER(da.description) LIKE LOWER(CONCAT('%', :search, '%'))")
	Page<DetectionAnalytic> searchAnalytics(@Param("search") String search, Pageable pageable);

	@Query("SELECT da FROM DetectionAnalytic da WHERE da.lastExecuted < :threshold AND da.isActive = true")
	List<DetectionAnalytic> findStaleAnalytics(@Param("threshold") LocalDateTime threshold);

	@Query("SELECT da.platform, COUNT(da) FROM DetectionAnalytic da GROUP BY da.platform")
	List<Object[]> countByPlatform();

	@Query("SELECT da.createdBy, COUNT(da) FROM DetectionAnalytic da GROUP BY da.createdBy")
	List<Object[]> countByCreator();

	@Query("SELECT COUNT(da) FROM DetectionAnalytic da WHERE da.isActive = true")
	long countActiveAnalytics();

	@Query("SELECT AVG(da.accuracy) FROM DetectionAnalytic da WHERE da.accuracy IS NOT NULL")
	BigDecimal getAverageAccuracy();
}
