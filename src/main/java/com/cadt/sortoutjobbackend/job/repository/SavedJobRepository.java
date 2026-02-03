package com.cadt.sortoutjobbackend.job.repository;

import com.cadt.sortoutjobbackend.job.entity.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    // Find all saved jobs by user
    Page<SavedJob> findByUserIdOrderBySavedAtDesc(Long userId, Pageable pageable);

    // Find saved job by user and job
    Optional<SavedJob> findByUserIdAndJobId(Long userId, Long jobId);

    // Check if user has saved a job
    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    // Delete saved job (unsave)
    void deleteByUserIdAndJobId(Long userId, Long jobId);

    // Count user's saved jobs
    long countByUserId(Long userId);

    // Get job IDs that user has saved (for UI display)
    @Query("SELECT sj.job.id FROM SavedJob sj WHERE sj.user.id = :userId")
    List<Long> findJobIdsByUserId(@Param("userId") Long userId);
}
