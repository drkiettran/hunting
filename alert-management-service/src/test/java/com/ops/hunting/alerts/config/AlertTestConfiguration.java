package com.ops.hunting.alerts.config;

import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

@TestConfiguration
public class AlertTestConfiguration {

	@Bean
	@Primary
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return mock(KafkaTemplate.class);
	}

	@Bean
	public EmbeddedKafkaBroker embeddedKafkaBroker() {
		// return new EmbeddedKafkaBroker(1, true, "alert-created",
		// "alert-status-updated", "alert-deleted");
		return null;
	}
}