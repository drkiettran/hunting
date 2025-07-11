package com.ops.hunting.user.repository;

import com.ops.hunting.user.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, String> {

	Optional<UserCredential> findByUserId(String userId);

	void deleteByUserId(String userId);
}