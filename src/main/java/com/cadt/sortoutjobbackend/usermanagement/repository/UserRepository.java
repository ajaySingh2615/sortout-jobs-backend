package com.cadt.sortoutjobbackend.usermanagement.repository;

import com.cadt.sortoutjobbackend.usermanagement.entity.AuthProvider;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    // Count methods for stats
    long countByRole(String role);

    long countByIsActiveTrue();

    long countByIsActiveFalse();

    long countByCreatedAtAfter(Instant date);

    // Search with filters
    @Query("SELECT u FROM User u WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "u.phone LIKE CONCAT('%', :search, '%')) " +
           "AND (:role IS NULL OR :role = '' OR u.role = :role) " +
           "AND (:isActive IS NULL OR u.isActive = :isActive) " +
           "AND (:authProvider IS NULL OR u.authProvider = :authProvider) " +
           "ORDER BY u.createdAt DESC")
    Page<User> searchUsers(
            @Param("search") String search,
            @Param("role") String role,
            @Param("isActive") Boolean isActive,
            @Param("authProvider") AuthProvider authProvider,
            Pageable pageable
    );
}
