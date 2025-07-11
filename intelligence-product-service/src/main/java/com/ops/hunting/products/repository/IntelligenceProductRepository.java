package com.ops.hunting.products.repository;

import com.ops.hunting.common.enums.Classification;
import com.ops.hunting.common.enums.ProductType;
import com.ops.hunting.products.entity.IntelligenceProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IntelligenceProductRepository extends JpaRepository<IntelligenceProduct, String> {

	List<IntelligenceProduct> findByType(ProductType type);

	List<IntelligenceProduct> findByAuthor(String author);

	List<IntelligenceProduct> findByClassification(Classification classification);

	List<IntelligenceProduct> findByReviewStatus(IntelligenceProduct.ReviewStatus reviewStatus);

	List<IntelligenceProduct> findByPublishedDateIsNotNull();

	Page<IntelligenceProduct> findByType(ProductType type, Pageable pageable);

	Page<IntelligenceProduct> findByAuthor(String author, Pageable pageable);

	Page<IntelligenceProduct> findByClassification(Classification classification, Pageable pageable);

	Page<IntelligenceProduct> findByReviewStatus(IntelligenceProduct.ReviewStatus reviewStatus, Pageable pageable);

	@Query("SELECT ip FROM IntelligenceProduct ip WHERE " + "LOWER(ip.title) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "LOWER(ip.content) LIKE LOWER(CONCAT('%', :search, '%'))")
	Page<IntelligenceProduct> searchProducts(@Param("search") String search, Pageable pageable);

	@Query("SELECT ip FROM IntelligenceProduct ip WHERE ip.createdDate >= :startDate AND ip.createdDate <= :endDate")
	List<IntelligenceProduct> findByCreatedDateBetween(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("SELECT ip.type, COUNT(ip) FROM IntelligenceProduct ip GROUP BY ip.type")
	List<Object[]> countByType();

	@Query("SELECT ip.classification, COUNT(ip) FROM IntelligenceProduct ip GROUP BY ip.classification")
	List<Object[]> countByClassification();

	@Query("SELECT ip.author, COUNT(ip) FROM IntelligenceProduct ip GROUP BY ip.author")
	List<Object[]> countByAuthor();

	@Query("SELECT COUNT(ip) FROM IntelligenceProduct ip WHERE ip.publishedDate IS NOT NULL")
	long countPublishedProducts();

	@Query("SELECT COUNT(ip) FROM IntelligenceProduct ip WHERE ip.reviewStatus = 'UNDER_REVIEW'")
	long countProductsUnderReview();

	@Query("SELECT AVG(TIMESTAMPDIFF(HOUR, ip.createdDate, ip.publishedDate)) FROM IntelligenceProduct ip WHERE ip.publishedDate IS NOT NULL")
	Double getAverageTimeToPublishInHours();

	boolean existsByTitleAndAuthor(String title, String author);
}
