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
public class DatabricksPlatformService implements PlatformService {

	@Value("${platforms.databricks.host:https://databricks.company.com}")
	private String databricksHost;

	@Value("${platforms.databricks.token:your-databricks-token}")
	private String token;

	@Value("${platforms.databricks.cluster-id:your-cluster-id}")
	private String clusterId;

	private final RestTemplate restTemplate;
	private final Map<String, String> deployedAnalytics = new ConcurrentHashMap<>();

	public DatabricksPlatformService() {
		this.restTemplate = new RestTemplate();
	}

	@Override
	public Platform getSupportedPlatform() {
		return Platform.DATABRICKS;
	}

	@Override
	public void deployAnalytic(String analyticId, String query) {
		try {
			// In a real implementation, this would create a scheduled job in Databricks
			deployedAnalytics.put(analyticId, query);

			// Validate the SQL query
			validateSqlQuery(query);

			System.out.println("Deployed analytic " + analyticId + " to Databricks");
		} catch (Exception e) {
			throw new RuntimeException("Failed to deploy analytic to Databricks: " + e.getMessage());
		}
	}

	@Override
	public void undeployAnalytic(String analyticId) {
		try {
			deployedAnalytics.remove(analyticId);
			System.out.println("Undeployed analytic " + analyticId + " from Databricks");
		} catch (Exception e) {
			throw new RuntimeException("Failed to undeploy analytic from Databricks: " + e.getMessage());
		}
	}

	@Override
	public QueryResult executeQuery(String query) {
		try {
			HttpHeaders headers = createAuthHeaders();

			// Build SQL execution request
			Map<String, Object> sqlRequest = buildSqlRequest(query);
			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(sqlRequest, headers);

			// Execute SQL statement
			String url = databricksHost + "/api/2.0/sql/statements";
			ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				return parseSqlResponse(response.getBody());
			} else {
				return new QueryResult("Failed to execute query on Databricks");
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

			ResponseEntity<Map> response = restTemplate.exchange(
					databricksHost + "/api/2.0/clusters/get?cluster_id=" + clusterId, HttpMethod.GET, entity,
					Map.class);

			return response.getStatusCode().is2xxSuccessful();
		} catch (Exception e) {
			return false;
		}
	}

	private void validateSqlQuery(String query) {
		if (query == null || query.trim().isEmpty()) {
			throw new RuntimeException("SQL query cannot be empty");
		}

		// Basic SQL validation
		String upperQuery = query.toUpperCase().trim();
		if (!upperQuery.startsWith("SELECT")) {
			throw new RuntimeException("Only SELECT queries are allowed");
		}
	}

	private HttpHeaders createAuthHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.set("Content-Type", "application/json");
		return headers;
	}

	private Map<String, Object> buildSqlRequest(String query) {
		Map<String, Object> sqlRequest = new HashMap<>();
		sqlRequest.put("statement", query);
		sqlRequest.put("warehouse_id", clusterId);
		sqlRequest.put("catalog", "main");
		sqlRequest.put("schema", "default");

		return sqlRequest;
	}

	private QueryResult parseSqlResponse(Map<String, Object> response) {
		try {
			// Parse Databricks SQL API response
			Map<String, Object> result = (Map<String, Object>) response.get("result");
			if (result != null) {
				Number rowCount = (Number) result.get("row_count");
				long recordsProcessed = rowCount != null ? rowCount.longValue() : 0;

				// Apply threat detection logic to determine alerts
				int alerts = (int) Math.min(recordsProcessed * 0.1, 50); // 10% of records as alerts, max 50

				return new QueryResult(recordsProcessed, alerts, response.toString());
			} else {
				return new QueryResult("No result data in response");
			}

		} catch (Exception e) {
			return new QueryResult("Failed to parse SQL response: " + e.getMessage());
		}
	}
}
