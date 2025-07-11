package com.ops.hunting.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackages = { "com.ops.hunting.common.entity", "com.ops.hunting.analytics.entity" })
@EnableCaching
@EnableKafka
@EnableScheduling
public class DetectionAnalyticsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DetectionAnalyticsServiceApplication.class, args);
	}
}
