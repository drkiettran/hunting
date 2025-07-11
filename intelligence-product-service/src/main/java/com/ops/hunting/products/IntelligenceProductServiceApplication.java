package com.ops.hunting.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EntityScan(basePackages = { "com.ops.hunting.common.entity", "com.ops.hunting.products.entity" })
@EnableCaching
@EnableKafka
public class IntelligenceProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntelligenceProductServiceApplication.class, args);
	}
}
