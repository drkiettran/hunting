package com.ops.hunting.analytics.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ops.hunting.analytics.service.DetectionAnalyticService;

@Component
public class AnalyticsExecutionScheduler {

	private final DetectionAnalyticService analyticService;

	@Value("${analytics.execution.enabled:true}")
	private boolean executionEnabled;

	@Autowired
	public AnalyticsExecutionScheduler(DetectionAnalyticService analyticService) {
		this.analyticService = analyticService;
	}

	@Scheduled(fixedRateString = "${analytics.execution.interval-minutes:5}000")
	public void executeActiveAnalytics() {
		if (executionEnabled) {
			try {
				System.out.println("Starting scheduled execution of active analytics...");
				analyticService.executeAllActiveAnalytics();
				System.out.println("Completed scheduled execution of active analytics");
			} catch (Exception e) {
				System.err.println("Error during scheduled analytics execution: " + e.getMessage());
			}
		}
	}
}
