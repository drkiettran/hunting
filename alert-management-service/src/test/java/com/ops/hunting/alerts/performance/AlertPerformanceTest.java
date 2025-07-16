package com.ops.hunting.alerts.performance;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import com.ops.hunting.alerts.dto.AlertDTO;
import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.enums.AlertStatus;
import com.ops.hunting.alerts.service.AlertService;

@SpringBootTest
@ActiveProfiles("test")
public class AlertPerformanceTest {

	@Autowired
	private AlertService alertService;

	@Test
	void bulkCreateAlerts_ShouldCompleteWithinTimeLimit() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		List<AlertDTO> alerts = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			AlertDTO alert = AlertDTO.builder().title("Performance Test Alert " + i)
					.description("Bulk create performance test").severity(AlertSeverity.MEDIUM).status(AlertStatus.OPEN)
					.sourceSystem("PERF_TEST").createdAt(LocalDateTime.now()).build();
			alerts.add(alert);
		}

		// Create alerts in parallel
		ExecutorService executor = Executors.newFixedThreadPool(10);
		List<CompletableFuture<Void>> futures = new ArrayList<>();

		for (AlertDTO alert : alerts) {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				alertService.createAlert(alert);
			}, executor);
			futures.add(future);
		}

		// Wait for all to complete
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		stopWatch.stop();
		executor.shutdown();

		// Should complete within 30 seconds
		assertThat(stopWatch.getTotalTimeMillis()).isLessThan(30000);
	}

	@Test
	void searchAlerts_ShouldBeEfficient() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// Perform multiple search operations
		for (int i = 0; i < 100; i++) {
			alertService.searchAlerts(AlertSeverity.HIGH, AlertStatus.OPEN, LocalDateTime.now().minusDays(1),
					LocalDateTime.now(), org.springframework.data.domain.PageRequest.of(0, 10));
		}

		stopWatch.stop();

		// Should complete within 5 seconds
		assertThat(stopWatch.getTotalTimeMillis()).isLessThan(5000);
	}
}