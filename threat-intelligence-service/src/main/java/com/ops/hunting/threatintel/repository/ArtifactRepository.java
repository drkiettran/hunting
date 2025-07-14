package com.ops.hunting.threatintel.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ops.hunting.threatintel.entity.Artifact;
import com.ops.hunting.threatintel.entity.ArtifactType;
import com.ops.hunting.threatintel.entity.IocType;

@Repository
public interface ArtifactRepository extends JpaRepository<Artifact, Long> {

	Optional<Artifact> findByArtifactId(String artifactId);

	List<Artifact> findByArtifactTypeAndActiveTrue(ArtifactType artifactType);

	List<Artifact> findBySourceAndActiveTrue(String source);

	List<Artifact> findByIocValueAndIocType(String iocValue, IocType iocType);

	List<Artifact> findByThreatActorAndActiveTrue(String threatActor);

	List<Artifact> findByMalwareFamilyAndActiveTrue(String malwareFamily);

	List<Artifact> findByCampaignAndActiveTrue(String campaign);

	@Query("SELECT a FROM Artifact a WHERE a.active = true AND "
			+ "(a.expirationTime IS NULL OR a.expirationTime > :currentTime)")
	List<Artifact> findActiveNonExpiredArtifacts(@Param("currentTime") LocalDateTime currentTime);

	@Query("SELECT a FROM Artifact a WHERE a.timestamp BETWEEN :startTime AND :endTime")
	List<Artifact> findByTimestampBetween(@Param("startTime") LocalDateTime startTime,
			@Param("endTime") LocalDateTime endTime);

	@Query("SELECT COUNT(a) FROM Artifact a WHERE a.artifactType = :type AND a.active = true")
	Long countByArtifactTypeAndActive(@Param("type") ArtifactType type);
}
