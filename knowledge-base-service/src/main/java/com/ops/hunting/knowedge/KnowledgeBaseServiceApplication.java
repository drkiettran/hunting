package com.ops.hunting.knowedge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EntityScan(basePackages = { "com.ops.hunting.common.entity", "com.ops.hunting.knowledge.entity" })
@EnableCaching
@EnableKafka
public class KnowledgeBaseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnowledgeBaseServiceApplication.class, args);
	}
}
