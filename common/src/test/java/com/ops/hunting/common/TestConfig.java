package com.ops.hunting.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.ops.hunting.common.dto.UserRequest;
import com.ops.hunting.common.entity.Alert;
import com.ops.hunting.common.entity.ThreatIntel;
import com.ops.hunting.common.enums.UserRole;

import jakarta.validation.Validator;

@TestConfiguration
class TestConfig {

	@Bean
	@Primary
	public Validator validator() {
		LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
		factory.afterPropertiesSet();
		return factory.getValidator();
	}
}

// Test data builders and utilities
class TestDataBuilder {

	public static ThreatIntel.ThreatIntelBuilder createThreatIntelBuilder() {
		return ThreatIntel.builder().iocType("IP").iocValue("192.168.1.100").threatLevel("HIGH").source("Test Source")
				.confidence(85).description("Test threat intel");
	}

	public static Alert.AlertBuilder createAlertBuilder() {
		return Alert.builder().title("Test Alert").description("Test alert description").severity("MEDIUM")
				.status("OPEN").ruleId("TEST_RULE_001");
	}

	public static UserRequest.UserRequestBuilder createUserRequestBuilder() {
		return UserRequest.builder().username("testuser").email("test@example.com").firstName("Test").lastName("User")
				.role(UserRole.ANALYST).password("TestPass123!");
	}
}