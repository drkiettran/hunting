package com.ops.hunting.alerts.cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import com.ops.hunting.alerts.dto.AlertDTO;
import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.enums.AlertStatus;
import com.ops.hunting.alerts.service.AlertService;

@SpringBootTest
@ActiveProfiles("test")
public class AlertCacheTest {

	@Autowired
	private AlertService alertService;

	@Autowired
	private CacheManager cacheManager;

	@Test
	void getAlertById_ShouldCacheResult() {
		// Create alert first
		AlertDTO alertDTO = AlertDTO.builder().title("Cache Test Alert").severity(AlertSeverity.MEDIUM)
				.status(AlertStatus.OPEN).sourceSystem("TEST_SYSTEM").createdAt(LocalDateTime.now()).build();

		AlertDTO createdAlert = alertService.createAlert(alertDTO);
		UUID alertId = createdAlert.getId();

		// First call - should cache the result
		AlertDTO firstCall = alertService.getAlertById(alertId);

		// Check cache
		Cache alertCache = cacheManager.getCache("alerts");
		assertThat(alertCache).isNotNull();
		assertThat(alertCache.get(alertId)).isNotNull();

		// Second call - should return cached result
		AlertDTO secondCall = alertService.getAlertById(alertId);

		assertThat(firstCall).isEqualTo(secondCall);
	}

	@Test
	void updateAlertStatus_ShouldEvictCache() {
		// Create alert
		AlertDTO alertDTO = AlertDTO.builder().title("Cache Eviction Test Alert").severity(AlertSeverity.HIGH)
				.status(AlertStatus.OPEN).sourceSystem("TEST_SYSTEM").createdAt(LocalDateTime.now()).build();

		AlertDTO createdAlert = alertService.createAlert(alertDTO);
		UUID alertId = createdAlert.getId();

		// Cache the alert
		alertService.getAlertById(alertId);

		Cache alertCache = cacheManager.getCache("alerts");
		assertThat(alertCache.get(alertId)).isNotNull();

		// Update status - should evict cache
		alertService.updateAlertStatus(alertId, AlertStatus.IN_PROGRESS, "analyst1");

		// Cache should be evicted
		assertThat(alertCache.get(alertId)).isNull();
	}
}