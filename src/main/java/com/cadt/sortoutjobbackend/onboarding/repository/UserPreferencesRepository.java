package com.cadt.sortoutjobbackend.onboarding.repository;

import com.cadt.sortoutjobbackend.onboarding.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    Optional<UserPreferences> findByUserId(Long userId);

    @Query("SELECT up FROM UserPreferences up " +
           "LEFT JOIN FETCH up.preferredCity " +
           "LEFT JOIN FETCH up.preferredLocality " +
           "LEFT JOIN FETCH up.preferredRole " +
           "LEFT JOIN FETCH up.skills " +
           "WHERE up.user.id = :userId")
    Optional<UserPreferences> findByUserIdWithDetails(@Param("userId") Long userId);
}

