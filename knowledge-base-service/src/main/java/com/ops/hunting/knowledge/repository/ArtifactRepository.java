package com.ops.hunting.knowledge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ops.hunting.common.enums.ArtifactType;
import com.ops.hunting.knowledge.entity.Artifact;

@Repository
public interface ArtifactRepository extends JpaRepository<Artifact, String> {

	List<Artifact> findByTypeAndIsArchivedFalse(ArtifactType type);

	List<Artifact> findByCreatedByAndIsArchivedFalse(String createdBy);

	List<Artifact> findByInvestigationIdAndIsArchivedFalse(String investigationId);

	Page<Artifact> findByIsArchivedFalse(Pageable pageable);

	Page<Artifact> findByIdIn(List<String> ids, Pageable pageable);

	Optional<Artifact> findByChecksum(String checksum);

	@Query("SELECT a FROM Artifact a WHERE a.isArchived = false AND "
			+ "(LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "LOWER(a.description) LIKE LOWER(CONCAT('%', :search, '%')))")
	Page<Artifact> searchArtifacts(@Param("search") String search, Pageable pageable);

	@Query("SELECT a.type, COUNT(a) FROM Artifact a WHERE a.isArchived = false GROUP BY a.type")
	List<Object[]> countByType();

	@Query("SELECT a.createdBy, COUNT(a) FROM Artifact a WHERE a.isArchived = false GROUP BY a.createdBy")
	List<Object[]> countByCreator();

	@Query("SELECT COUNT(a) FROM Artifact a WHERE a.isArchived = false")
	long countByIsArchivedFalse();

	@Query("SELECT COALESCE(SUM(a.fileSize), 0) FROM Artifact a WHERE a.isArchived = false")
	long sumFileSizeByIsArchivedFalse();
}