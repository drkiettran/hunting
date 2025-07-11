package com.ops.hunting.knowledge.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ops.hunting.knowledge.entity.Artifact;

@Service
public class SearchService {

	private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
	private static final String ARTIFACT_INDEX = "artifacts";

	private final RestHighLevelClient elasticsearchClient;
	private final ObjectMapper objectMapper;

	@Value("${elasticsearch.enabled:true}")
	private boolean elasticsearchEnabled;

	@Autowired
	public SearchService(RestHighLevelClient elasticsearchClient, ObjectMapper objectMapper) {
		this.elasticsearchClient = elasticsearchClient;
		this.objectMapper = objectMapper;
	}

	public void indexArtifact(Artifact artifact) {
		if (!elasticsearchEnabled) {
			logger.debug("Elasticsearch is disabled, skipping artifact indexing");
			return;
		}

		try {
			Map<String, Object> document = createArtifactDocument(artifact);

			IndexRequest request = new IndexRequest(ARTIFACT_INDEX).id(artifact.getId()).source(document,
					XContentType.JSON);

			elasticsearchClient.index(request, RequestOptions.DEFAULT);
			logger.debug("Indexed artifact: {}", artifact.getId());
		} catch (Exception e) {
			logger.error("Failed to index artifact: {}", artifact.getId(), e);
			// Don't throw exception to avoid breaking the main flow
		}
	}

	public List<String> searchArtifacts(String query, int size, int from) {
		if (!elasticsearchEnabled) {
			logger.debug("Elasticsearch is disabled, returning empty search results");
			return new ArrayList<>();
		}

		try {
			SearchRequest searchRequest = new SearchRequest(ARTIFACT_INDEX);
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

			// Build multi-field search query
			var multiMatchQuery = QueryBuilders.multiMatchQuery(query).field("name", 2.0f) // Higher weight for name
					.field("description", 1.5f).field("content", 1.0f).field("metadata.*", 0.5f)
					.type(org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.BEST_FIELDS);

			searchSourceBuilder.query(multiMatchQuery);
			searchSourceBuilder.size(size);
			searchSourceBuilder.from(from);

			// Add highlighting
			searchSourceBuilder.highlighter(new org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder()
					.field("name").field("description").field("content"));

			searchRequest.source(searchSourceBuilder);

			SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);

			List<String> artifactIds = new ArrayList<>();
			for (SearchHit hit : searchResponse.getHits().getHits()) {
				artifactIds.add(hit.getId());
			}

			logger.debug("Search query '{}' returned {} results", query, artifactIds.size());
			return artifactIds;
		} catch (Exception e) {
			logger.error("Failed to search artifacts with query: {}", query, e);
			return new ArrayList<>();
		}
	}

	public void removeArtifactFromIndex(String artifactId) {
		if (!elasticsearchEnabled) {
			logger.debug("Elasticsearch is disabled, skipping artifact removal");
			return;
		}

		try {
			DeleteRequest deleteRequest = new DeleteRequest(ARTIFACT_INDEX, artifactId);
			elasticsearchClient.delete(deleteRequest, RequestOptions.DEFAULT);
			logger.debug("Removed artifact from index: {}", artifactId);
		} catch (Exception e) {
			logger.error("Failed to remove artifact from index: {}", artifactId, e);
			// Don't throw exception to avoid breaking the main flow
		}
	}

	public void bulkIndexArtifacts(List<Artifact> artifacts) {
		if (!elasticsearchEnabled) {
			logger.debug("Elasticsearch is disabled, skipping bulk indexing");
			return;
		}

		try {
			for (Artifact artifact : artifacts) {
				indexArtifact(artifact);
			}
			logger.debug("Bulk indexed {} artifacts", artifacts.size());
		} catch (Exception e) {
			logger.error("Failed to bulk index artifacts", e);
		}
	}

	private Map<String, Object> createArtifactDocument(Artifact artifact) {
		Map<String, Object> document = new HashMap<>();
		document.put("id", artifact.getId());
		document.put("name", artifact.getName());
		document.put("type", artifact.getType().name());
		document.put("description", artifact.getDescription());
		document.put("createdBy", artifact.getCreatedBy());
		document.put("investigationId", artifact.getInvestigationId());
		document.put("fileSize", artifact.getFileSize());
		document.put("contentType", artifact.getContentType());
		document.put("createdDate", artifact.getCreatedDate());
		document.put("isArchived", artifact.getIsArchived());

		// Add metadata
		if (artifact.getMetadata() != null && !artifact.getMetadata().isEmpty()) {
			document.put("metadata", artifact.getMetadata());
		}

		// Add searchable content based on file type
		String searchableContent = extractSearchableContent(artifact);
		if (searchableContent != null) {
			document.put("content", searchableContent);
		}

		return document;
	}

	private String extractSearchableContent(Artifact artifact) {
		// This would typically extract text content from files for indexing
		// For now, we'll use the description and metadata as searchable content
		StringBuilder content = new StringBuilder();

		if (artifact.getDescription() != null) {
			content.append(artifact.getDescription()).append(" ");
		}

		if (artifact.getMetadata() != null) {
			artifact.getMetadata().values().forEach(value -> content.append(value).append(" "));
		}

		return content.toString().trim();
	}
}