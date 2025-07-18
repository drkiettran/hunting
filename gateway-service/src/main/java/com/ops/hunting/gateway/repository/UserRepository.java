package com.ops.hunting.gateway.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ops.hunting.gateway.entity.User;

import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {
	Mono<User> findByUsername(String username);

	Mono<User> findByEmail(String email);

	Mono<Boolean> existsByUsername(String username);

	Mono<Boolean> existsByEmail(String email);
}