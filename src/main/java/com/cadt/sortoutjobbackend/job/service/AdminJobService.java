package com.cadt.sortoutjobbackend.job.service;

import com.cadt.sortoutjobbackend.job.dto.*;

public interface AdminJobService {
    
    // Job CRUD
    JobDTO createJob(Long adminId, CreateJobRequest request);
    
    JobDTO updateJob(Long jobId, UpdateJobRequest request);
    
    void deleteJob(Long jobId);
    
    void toggleJobStatus(Long jobId, boolean isActive);
    
    // List jobs for admin (includes inactive)
    JobListResponse getAllJobsForAdmin(int page, int size);
    
    // Stats
    AdminStatsDTO getAdminStats();
    
    // Application management
    void updateApplicationStatus(Long applicationId, String status, String notes);
    
    JobListResponse getApplicationsForJob(Long jobId, int page, int size);
}
