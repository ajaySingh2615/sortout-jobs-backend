package com.cadt.sortoutjobbackend.profile.repository;

import com.cadt.sortoutjobbackend.profile.entity.ResumeHeadline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeHeadlineRepository extends JpaRepository<ResumeHeadline, Long> {
    Optional<ResumeHeadline> findByUserId(Long userId);
}
