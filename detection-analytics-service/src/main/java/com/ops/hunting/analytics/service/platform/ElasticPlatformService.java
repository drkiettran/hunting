package com.ops.hunting.analytics.service.platform;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ops.hunting.common.enums.Platform;

@Service
public class ElasticPlatformService implements PlatformService {

	@Value("${platforms.elastic.host:http://localhost:9200}")
	private String elasticHost;

	@Value("${platforms.elastic.username:elastic}")
	private String username;

	@Value("${platforms.elastic.password:changeme}")
	private String password;

	@Value("${platforms.elastic.index-pattern:logs-*}")
	private String indexPattern;

	private final RestTemplate restTemplate;
	private final Map<String, String> deployedAnalytics = new ConcurrentHashMap<>();

	public ElasticPlatformService() {
		this.restTemplate = new RestTemplate();
	}

	@Override
	public Platform getSupportedPlatform() {
		return Platform.ELASTIC;
	}

	@Override
	public void deployAnalytic(String analyticId, String query) {
		try {
			// In a real implementation, this would deploy the query as a saved search or
			// watcher
			// For this example, we'll just store it in memory
			deployedAnalytics.put(analyticId, query);

			// Validate the query by executing a test search
			testQuery(query);

			System.out.println("Deployed analytic " + analyticId + " to Elastic");
		} catch (Exception e) {
			throw new RuntimeException("Failed to deploy analytic to Elastic: " + e.getMessage());
		}
	}

	@Override
	public void undeployAnalytic(String analyticId) {
		try {
			deployedAnalytics.remove(analyticId);
			System.out.println("Undeployed analytic " + analyticId + " from Elastic");
		} catch (Exception e) {
			throw new RuntimeException("Failed to undeploy analytic from Elastic: " + e.getMessage());
		}
	}

	@Override
	public QueryResult executeQuery(String query) {
		try {
			HttpHeaders headers = createAuthHeaders();

			// Build Elasticsearch query
			Map<String, Object> searchRequest = buildSearchRequest(query);
			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(searchRequest, headers);

			// Execute search
			String url = elasticHost + "/" + indexPattern + "/_search";
			ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				return parseSearchResponse(response.getBody());
			} else {
				return new QueryResult("Failed to execute query on Elastic");
			}

		} catch (Exception e) {
			return new QueryResult("Error executing query: " + e.getMessage());
		}
	}

	@Override
	public boolean testConnection() {
		try {
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<Map> response = restTemplate.exchange(elasticHost + "/_cluster/health", HttpMethod.GET,
					entity, Map.class);

			return response.getStatusCode().is2xxSuccessful();
		} catch (Exception e) {
			return false;
		}
	}

	private void testQuery(String query) {
		// Simple validation - in real implementation would parse and validate DSL
		if (query == null || query.trim().isEmpty()) {
			throw new RuntimeException("Query cannot be empty");
		}
	}

	private HttpHeaders createAuthHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(username, password);
		headers.set("Content-Type", "application/json");
		return headers;
	}

	private Map<String, Object> buildSearchRequest(String query) {
		Map<String, Object> searchRequest = new HashMap<>();

		// For this example, we'll use a simple query string query
		// In a real implementation, this would parse the custom query format
		Map<String, Object> queryMap = new HashMap<>();
		Map<String, Object> queryString = new HashMap<>();
		queryString.put("query", query);
		queryMap.put("query_string", queryString);
		searchRequest.put("query", queryMap);

		// Limit results for performance
		searchRequest.put("size", 1000);

		return searchRequest;
	}

	private QueryResult parseSearchResponse(Map<String, Object> response) {
		try {
			Map<String, Object> hits = (Map<String, Object>) response.get("hits");
			Object totalObj = hits.get("total");

			long totalHits;
			if (totalObj instanceof Map) {
				totalHits = ((Number) ((Map<String, Object>) totalObj).get("value")).longValue();
			} else {
				totalHits = ((Number) totalObj).longValue();
			}

			// For this example, assume all hits are potential alerts
			// In real implementation, would apply threat detection logic
			int alerts = Math.min((int) totalHits, 100); // Cap at 100 alerts per execution

			return new QueryResult(totalHits, alerts, response.toString());

		} catch (Exception e) {
			return new QueryResult("Failed to parse search response: " + e.getMessage());
		}
	}
}
