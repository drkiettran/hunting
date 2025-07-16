package com.ops.hunting.alerts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableCaching
@EnableKafka
@EnableMethodSecurity(prePostEnabled = true)
public class AlertManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlertManagementServiceApplication.class, args);
	}
}