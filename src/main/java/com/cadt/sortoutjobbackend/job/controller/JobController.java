package com.cadt.sortoutjobbackend.job.controller;

import com.cadt.sortoutjobbackend.common.dto.ApiResponse;
import com.cadt.sortoutjobbackend.job.dto.*;
import com.cadt.sortoutjobbackend.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    // ==================== JOB LISTING ====================

    /**
     * Get all active jobs with pagination (public - no auth needed for browsing)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<JobListResponse>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        JobListResponse response = jobService.getAllJobs(page, size);
        return ResponseEntity.ok(ApiResponse.success("Jobs fetched successfully", response));
    }

    /**
     * Search jobs with filters
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<JobListResponse>> searchJobs(@RequestBody JobSearchRequest request) {
        JobListResponse response = jobService.searchJobs(request);
        return ResponseEntity.ok(ApiResponse.success("Jobs fetched successfully", response));
    }

    /**
     * Get recommended jobs for a user based on their profile preferences
     */
    @GetMapping("/recommended/{userId}")
    public ResponseEntity<ApiResponse<JobListResponse>> getRecommendedJobs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        JobListResponse response = jobService.getRecommendedJobs(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Recommended jobs fetched successfully", response));
    }

    /**
     * Get job details by ID
     */
    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobDTO>> getJobById(
            @PathVariable Long jobId,
            @RequestParam(required = false) Long userId) {
        JobDTO response = jobService.getJobById(jobId, userId);
        return ResponseEntity.ok(ApiResponse.success("Job details fetched successfully", response));
    }

    // ==================== SAVED JOBS ====================

    /**
     * Save a job (bookmark)
     */
    @PostMapping("/{jobId}/save/{userId}")
    public ResponseEntity<ApiResponse<Void>> saveJob(
            @PathVariable Long userId,
            @PathVariable Long jobId) {
        jobService.saveJob(userId, jobId);
        return ResponseEntity.ok(ApiResponse.success("Job saved successfully"));
    }

    /**
     * Unsave a job (remove bookmark)
     */
    @DeleteMapping("/{jobId}/save/{userId}")
    public ResponseEntity<ApiResponse<Void>> unsaveJob(
            @PathVariable Long userId,
            @PathVariable Long jobId) {
        jobService.unsaveJob(userId, jobId);
        return ResponseEntity.ok(ApiResponse.success("Job removed from saved"));
    }

    /**
     * Get user's saved jobs
     */
    @GetMapping("/saved/{userId}")
    public ResponseEntity<ApiResponse<JobListResponse>> getSavedJobs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        JobListResponse response = jobService.getSavedJobs(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Saved jobs fetched successfully", response));
    }

    /**
     * Get IDs of saved jobs (for quick lookup in frontend)
     */
    @GetMapping("/saved/{userId}/ids")
    public ResponseEntity<ApiResponse<List<Long>>> getSavedJobIds(@PathVariable Long userId) {
        List<Long> ids = jobService.getSavedJobIds(userId);
        return ResponseEntity.ok(ApiResponse.success("Saved job IDs fetched", ids));
    }

    // ==================== JOB APPLICATIONS ====================

    /**
     * Apply to a job
     */
    @PostMapping("/{jobId}/apply/{userId}")
    public ResponseEntity<ApiResponse<JobApplicationDTO>> applyToJob(
            @PathVariable Long userId,
            @PathVariable Long jobId,
            @RequestBody(required = false) ApplyJobRequest request) {
        if (request == null) {
            request = new ApplyJobRequest();
        }
        JobApplicationDTO response = jobService.applyToJob(userId, jobId, request);
        return ResponseEntity.ok(ApiResponse.success("Application submitted successfully", response));
    }

    /**
     * Get user's applications
     */
    @GetMapping("/applications/{userId}")
    public ResponseEntity<ApiResponse<List<JobApplicationDTO>>> getMyApplications(@PathVariable Long userId) {
        List<JobApplicationDTO> response = jobService.getMyApplications(userId);
        return ResponseEntity.ok(ApiResponse.success("Applications fetched successfully", response));
    }

    /**
     * Get specific application details
     */
    @GetMapping("/applications/{userId}/{applicationId}")
    public ResponseEntity<ApiResponse<JobApplicationDTO>> getApplicationById(
            @PathVariable Long userId,
            @PathVariable Long applicationId) {
        JobApplicationDTO response = jobService.getApplicationById(userId, applicationId);
        return ResponseEntity.ok(ApiResponse.success("Application details fetched", response));
    }

    /**
     * Get IDs of applied jobs (for quick lookup in frontend)
     */
    @GetMapping("/applied/{userId}/ids")
    public ResponseEntity<ApiResponse<List<Long>>> getAppliedJobIds(@PathVariable Long userId) {
        List<Long> ids = jobService.getAppliedJobIds(userId);
        return ResponseEntity.ok(ApiResponse.success("Applied job IDs fetched", ids));
    }

    // ==================== STATS ====================

    /**
     * Get job stats for dashboard
     */
    @GetMapping("/stats/{userId}")
    public ResponseEntity<ApiResponse<JobStatsDTO>> getJobStats(@PathVariable Long userId) {
        JobStatsDTO response = jobService.getJobStats(userId);
        return ResponseEntity.ok(ApiResponse.success("Stats fetched successfully", response));
    }
}
