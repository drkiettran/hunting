package com.ops.hunting.gateway.config;

//import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import reactor.core.publisher.Mono;

//@TestConfiguration
public class TestConfig {

	@Bean
	@Primary
	public ReactiveUserDetailsService testUserDetailsService() {
		return new ReactiveUserDetailsService() {
			private final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager(
					User.builder().username("admin").password("{noop}admin123").roles("ADMIN").build(),
					User.builder().username("analyst1").password("{noop}analyst123").roles("ANALYST").build());

			@Override
			public Mono<UserDetails> findByUsername(String username) {
				return Mono.fromCallable(() -> manager.loadUserByUsername(username));
			}
		};
	}
}