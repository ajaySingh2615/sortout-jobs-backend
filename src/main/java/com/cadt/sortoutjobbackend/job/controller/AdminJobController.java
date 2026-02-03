package com.cadt.sortoutjobbackend.job.controller;

import com.cadt.sortoutjobbackend.common.dto.ApiResponse;
import com.cadt.sortoutjobbackend.job.dto.*;
import com.cadt.sortoutjobbackend.job.service.AdminJobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
public class AdminJobController {

    private final AdminJobService adminJobService;

    // ==================== STATS ====================

    /**
     * Get admin dashboard stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsDTO>> getAdminStats() {
        AdminStatsDTO stats = adminJobService.getAdminStats();
        return ResponseEntity.ok(ApiResponse.success("Stats fetched successfully", stats));
    }

    // ==================== JOB CRUD ====================

    /**
     * Get all jobs for admin (includes inactive)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<JobListResponse>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        JobListResponse response = adminJobService.getAllJobsForAdmin(page, size);
        return ResponseEntity.ok(ApiResponse.success("Jobs fetched successfully", response));
    }

    /**
     * Create a new job
     */
    @PostMapping("/{adminId}")
    public ResponseEntity<ApiResponse<JobDTO>> createJob(
            @PathVariable Long adminId,
            @Valid @RequestBody CreateJobRequest request) {
        JobDTO job = adminJobService.createJob(adminId, request);
        return ResponseEntity.ok(ApiResponse.success("Job created successfully", job));
    }

    /**
     * Update an existing job
     */
    @PutMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobDTO>> updateJob(
            @PathVariable Long jobId,
            @RequestBody UpdateJobRequest request) {
        JobDTO job = adminJobService.updateJob(jobId, request);
        return ResponseEntity.ok(ApiResponse.success("Job updated successfully", job));
    }

    /**
     * Delete a job (soft delete)
     */
    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long jobId) {
        adminJobService.deleteJob(jobId);
        return ResponseEntity.ok(ApiResponse.success("Job deleted successfully"));
    }

    /**
     * Toggle job active status
     */
    @PatchMapping("/{jobId}/status")
    public ResponseEntity<ApiResponse<Void>> toggleJobStatus(
            @PathVariable Long jobId,
            @RequestParam boolean isActive) {
        adminJobService.toggleJobStatus(jobId, isActive);
        return ResponseEntity.ok(ApiResponse.success(isActive ? "Job activated" : "Job deactivated"));
    }

    // ==================== APPLICATION MANAGEMENT ====================

    /**
     * Update application status
     */
    @PatchMapping("/applications/{applicationId}/status")
    public ResponseEntity<ApiResponse<Void>> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam String status,
            @RequestParam(required = false) String notes) {
        adminJobService.updateApplicationStatus(applicationId, status, notes);
        return ResponseEntity.ok(ApiResponse.success("Application status updated"));
    }
}
