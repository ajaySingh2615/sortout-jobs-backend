package com.cadt.sortoutjobbackend.job.repository;

import com.cadt.sortoutjobbackend.job.entity.ApplicationStatus;
import com.cadt.sortoutjobbackend.job.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    void deleteByUser_Id(Long userId);

    @Modifying
    @Query("DELETE FROM JobApplication ja WHERE ja.job.id IN :jobIds")
    void deleteByJob_IdIn(@Param("jobIds") List<Long> jobIds);

    // Find all applications by user
    Page<JobApplication> findByUserIdOrderByAppliedAtDesc(Long userId, Pageable pageable);

    // Find all applications by user (without pagination)
    List<JobApplication> findByUserIdOrderByAppliedAtDesc(Long userId);

    // Find all applications for a job
    Page<JobApplication> findByJobIdOrderByAppliedAtDesc(Long jobId, Pageable pageable);

    // Find application by user and job (check if already applied)
    Optional<JobApplication> findByUserIdAndJobId(Long userId, Long jobId);

    // Check if user has applied to a job
    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    // Find applications by status
    Page<JobApplication> findByUserIdAndStatusOrderByAppliedAtDesc(Long userId, ApplicationStatus status, Pageable pageable);

    // Count user's applications
    long countByUserId(Long userId);

    // Count applications for a job
    long countByJobId(Long jobId);

    // Count applications by status for a user
    long countByUserIdAndStatus(Long userId, ApplicationStatus status);

    // Get job IDs that user has applied to (for UI display)
    @Query("SELECT ja.job.id FROM JobApplication ja WHERE ja.user.id = :userId")
    List<Long> findJobIdsByUserId(@Param("userId") Long userId);
}
