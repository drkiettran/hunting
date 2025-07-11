package com.ops.hunting.user.repository;

import com.ops.hunting.common.entity.User;
import com.ops.hunting.common.enums.AnalystTier;
import com.ops.hunting.common.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	List<User> findByRoleAndIsActiveTrue(UserRole role);

	List<User> findByTierAndIsActiveTrue(AnalystTier tier);

	Page<User> findByRoleAndIsActiveTrue(UserRole role, Pageable pageable);

	Page<User> findByIsActiveTrue(Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.isActive = true AND "
			+ "(LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
	Page<User> findActiveUsersBySearch(@Param("search") String search, Pageable pageable);

	@Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
	long countByRoleAndIsActiveTrue(@Param("role") UserRole role);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}
