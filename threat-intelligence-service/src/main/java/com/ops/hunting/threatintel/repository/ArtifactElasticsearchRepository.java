package com.ops.hunting.threatintel.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.ops.hunting.threatintel.entity.Artifact;

@Repository
public interface ArtifactElasticsearchRepository extends ElasticsearchRepository<Artifact, String> {

	List<Artifact> findByIocValue(String iocValue);

	List<Artifact> findByThreatActor(String threatActor);

	List<Artifact> findByMalwareFamily(String malwareFamily);

	List<Artifact> findByCampaign(String campaign);

	List<Artifact> findByTagsContaining(String tag);

	List<Artifact> findByMitreAttackTechniquesContaining(String technique);
}
