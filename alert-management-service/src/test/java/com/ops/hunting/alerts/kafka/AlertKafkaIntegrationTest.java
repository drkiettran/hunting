package com.ops.hunting.alerts.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.ops.hunting.alerts.dto.AlertDTO;
import com.ops.hunting.alerts.enums.AlertSeverity;
import com.ops.hunting.alerts.enums.AlertStatus;
import com.ops.hunting.alerts.service.AlertService;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "alert-created", "alert-status-updated", "alert-deleted" })
@ActiveProfiles("test")
@DirtiesContext
public class AlertKafkaIntegrationTest {

	@Autowired
	private AlertService alertService;

	private CountDownLatch latch = new CountDownLatch(1);
	private AlertDTO receivedAlert;

	@Test
	void createAlert_ShouldPublishToKafka() throws Exception {
		AlertDTO alertDTO = AlertDTO.builder().id(UUID.randomUUID()).title("Kafka Test Alert")
				.description("Test alert for Kafka integration").severity(AlertSeverity.HIGH).status(AlertStatus.OPEN)
				.sourceSystem("TEST_SYSTEM").createdAt(LocalDateTime.now()).build();

		// Create alert - this should trigger Kafka message
		alertService.createAlert(alertDTO);

		// Wait for Kafka message
		boolean messageReceived = latch.await(5, TimeUnit.SECONDS);
		assertThat(messageReceived).isTrue();
		assertThat(receivedAlert).isNotNull();
		assertThat(receivedAlert.getTitle()).isEqualTo("Kafka Test Alert");
	}

	@KafkaListener(topics = "alert-created", groupId = "test-group")
	void handleAlertCreated(ConsumerRecord<String, AlertDTO> record) {
		receivedAlert = record.value();
		latch.countDown();
	}
}