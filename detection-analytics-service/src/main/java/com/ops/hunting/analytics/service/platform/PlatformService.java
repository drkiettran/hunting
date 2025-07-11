package com.ops.hunting.analytics.service.platform;

import com.ops.hunting.common.enums.Platform;

public interface PlatformService {

	Platform getSupportedPlatform();

	void deployAnalytic(String analyticId, String query);

	void undeployAnalytic(String analyticId);

	QueryResult executeQuery(String query);

	boolean testConnection();

	class QueryResult {
		private long recordsProcessed;
		private int alertsGenerated;
		private String alertData;
		private boolean success;
		private String errorMessage;

		public QueryResult(long recordsProcessed, int alertsGenerated, String alertData) {
			this.recordsProcessed = recordsProcessed;
			this.alertsGenerated = alertsGenerated;
			this.alertData = alertData;
			this.success = true;
		}

		public QueryResult(String errorMessage) {
			this.errorMessage = errorMessage;
			this.success = false;
		}

		// Getters and setters
		public long getRecordsProcessed() {
			return recordsProcessed;
		}

		public void setRecordsProcessed(long recordsProcessed) {
			this.recordsProcessed = recordsProcessed;
		}

		public int getAlertsGenerated() {
			return alertsGenerated;
		}

		public void setAlertsGenerated(int alertsGenerated) {
			this.alertsGenerated = alertsGenerated;
		}

		public String getAlertData() {
			return alertData;
		}

		public void setAlertData(String alertData) {
			this.alertData = alertData;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
	}
}
