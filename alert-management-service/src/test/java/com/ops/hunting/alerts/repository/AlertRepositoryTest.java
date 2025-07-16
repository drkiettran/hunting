package com.ops.hunting.alerts.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.ops.hunting.alerts.entity.Alert;
import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.enums.AlertStatus;

@DataJpaTest
@ActiveProfiles("test")
public class AlertRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private AlertRepository alertRepository;

	private Alert alert1;
	private Alert alert2;
	private LocalDateTime baseTime;

	@BeforeEach
	void setUp() {
		baseTime = LocalDateTime.now();

		alert1 = Alert.builder().id(UUID.randomUUID()).title("High Severity Alert")
				.description("Critical security incident").severity(AlertSeverity.HIGH).status(AlertStatus.OPEN)
				.sourceSystem("IDS").sourceIp("192.168.1.100").createdAt(baseTime.minusHours(2)).build();

		alert2 = Alert.builder().id(UUID.randomUUID()).title("Medium Severity Alert")
				.description("Moderate security incident").severity(AlertSeverity.MEDIUM).status(AlertStatus.CLOSED)
				.sourceSystem("SIEM").sourceIp("192.168.1.101").createdAt(baseTime.minusHours(1)).build();

		entityManager.persistAndFlush(alert1);
		entityManager.persistAndFlush(alert2);
	}

	@Test
	void findById_ShouldReturnAlert() {
		Optional<Alert> found = alertRepository.findById(alert1.getId());

		assertThat(found).isPresent();
		assertThat(found.get().getTitle()).isEqualTo("High Severity Alert");
		assertThat(found.get().getSeverity()).isEqualTo(AlertSeverity.HIGH);
	}

	@Test
	void findByStatus_ShouldReturnAlertsWithSpecificStatus() {
		List<Alert> openAlerts = alertRepository.findByStatus(AlertStatus.OPEN);

		assertThat(openAlerts).hasSize(1);
		assertThat(openAlerts.get(0).getStatus()).isEqualTo(AlertStatus.OPEN);
	}

	@Test
	void findBySeverity_ShouldReturnAlertsWithSpecificSeverity() {
		List<Alert> highSeverityAlerts = alertRepository.findBySeverity(AlertSeverity.HIGH);

		assertThat(highSeverityAlerts).hasSize(1);
		assertThat(highSeverityAlerts.get(0).getSeverity()).isEqualTo(AlertSeverity.HIGH);
	}

	@Test
	void findByStatusAndSeverity_ShouldReturnFilteredAlerts() {
		List<Alert> alerts = alertRepository.findByStatusAndSeverity(AlertStatus.OPEN, AlertSeverity.HIGH);

		assertThat(alerts).hasSize(1);
		assertThat(alerts.get(0).getStatus()).isEqualTo(AlertStatus.OPEN);
		assertThat(alerts.get(0).getSeverity()).isEqualTo(AlertSeverity.HIGH);
	}

	@Test
	void findByCreatedAtBetween_ShouldReturnAlertsInTimeRange() {
		LocalDateTime from = baseTime.minusHours(3);
		LocalDateTime to = baseTime.minusMinutes(30);

		List<Alert> alerts = alertRepository.findByCreatedAtBetween(from, to);

		assertThat(alerts).hasSize(2);
	}

	@Test
	void findBySeverityAndStatusAndCreatedAtBetween_ShouldReturnFilteredResults() {
		LocalDateTime from = baseTime.minusHours(3);
		LocalDateTime to = baseTime;
		PageRequest pageable = PageRequest.of(0, 10);

		Page<Alert> alerts = alertRepository.findBySeverityAndStatusAndCreatedAtBetween(AlertSeverity.HIGH,
				AlertStatus.OPEN, from, to, pageable);

		assertThat(alerts.getContent()).hasSize(1);
		assertThat(alerts.getContent().get(0).getSeverity()).isEqualTo(AlertSeverity.HIGH);
		assertThat(alerts.getContent().get(0).getStatus()).isEqualTo(AlertStatus.OPEN);
	}

	@Test
	void countByStatus_ShouldReturnCorrectCount() {
		long openCount = alertRepository.countByStatus(AlertStatus.OPEN);
		long closedCount = alertRepository.countByStatus(AlertStatus.CLOSED);

		assertThat(openCount).isEqualTo(1);
		assertThat(closedCount).isEqualTo(1);
	}

	@Test
	void countBySeverity_ShouldReturnCorrectCount() {
		long highCount = alertRepository.countBySeverity(AlertSeverity.HIGH);
		long mediumCount = alertRepository.countBySeverity(AlertSeverity.MEDIUM);

		assertThat(highCount).isEqualTo(1);
		assertThat(mediumCount).isEqualTo(1);
	}

	@Test
	void findBySourceSystem_ShouldReturnAlertsFromSpecificSystem() {
		List<Alert> idsAlerts = alertRepository.findBySourceSystem("IDS");

		assertThat(idsAlerts).hasSize(1);
		assertThat(idsAlerts.get(0).getSourceSystem()).isEqualTo("IDS");
	}

	@Test
	void findByAssignedTo_ShouldReturnAssignedAlerts() {
		alert1.setAssignedTo("analyst1");
		entityManager.persistAndFlush(alert1);

		List<Alert> assignedAlerts = alertRepository.findByAssignedTo("analyst1");

		assertThat(assignedAlerts).hasSize(1);
		assertThat(assignedAlerts.get(0).getAssignedTo()).isEqualTo("analyst1");
	}

	@Test
	void deleteById_ShouldDeleteAlert() {
		UUID alertId = alert1.getId();
		alertRepository.deleteById(alertId);

		Optional<Alert> deleted = alertRepository.findById(alertId);
		assertThat(deleted).isNotPresent();
	}

	@Test
	void save_ShouldUpdateExistingAlert() {
		alert1.setStatus(AlertStatus.IN_PROGRESS);
		alert1.setAssignedTo("analyst2");
		alert1.setUpdatedAt(LocalDateTime.now());

		Alert updated = alertRepository.save(alert1);

		assertThat(updated.getStatus()).isEqualTo(AlertStatus.IN_PROGRESS);
		assertThat(updated.getAssignedTo()).isEqualTo("analyst2");
		assertThat(updated.getUpdatedAt()).isNotNull();
	}
}