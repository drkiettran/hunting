package com.ops.hunting.threatintel.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ops.hunting.common.enums.IndicatorType;
import com.ops.hunting.threatintel.entity.Indicator;

@Repository
public interface IndicatorRepository extends JpaRepository<Indicator, String> {

	List<Indicator> findByType(IndicatorType type);

	Optional<Indicator> findByTypeAndValue(IndicatorType type, String value);

	List<Indicator> findByConfidenceGreaterThanEqual(BigDecimal confidence);

	Page<Indicator> findByType(IndicatorType type, Pageable pageable);

	@Query("SELECT i FROM Indicator i WHERE " + "LOWER(i.value) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%'))")
	Page<Indicator> searchIndicators(@Param("search") String search, Pageable pageable);

	@Query("SELECT i.type, COUNT(i) FROM Indicator i GROUP BY i.type")
	List<Object[]> countByType();

	boolean existsByTypeAndValue(IndicatorType type, String value);
}
