package com.cadt.sortoutjobbackend.profile.repository;

import com.cadt.sortoutjobbackend.profile.entity.ProfileSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileSummaryRepository extends JpaRepository<ProfileSummary, Long> {
    Optional<ProfileSummary> findByUserId(Long userId);
}
