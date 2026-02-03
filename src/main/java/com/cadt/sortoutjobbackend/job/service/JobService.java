package com.cadt.sortoutjobbackend.job.service;

import com.cadt.sortoutjobbackend.job.dto.*;

import java.util.List;

public interface JobService {

    // Job Listing & Search
    JobListResponse getAllJobs(int page, int size);
    
    JobListResponse searchJobs(JobSearchRequest request);
    
    JobListResponse getRecommendedJobs(Long userId, int page, int size);
    
    JobDTO getJobById(Long jobId, Long userId);
    
    // Save/Unsave Jobs
    void saveJob(Long userId, Long jobId);
    
    void unsaveJob(Long userId, Long jobId);
    
    JobListResponse getSavedJobs(Long userId, int page, int size);
    
    // Apply to Jobs
    JobApplicationDTO applyToJob(Long userId, Long jobId, ApplyJobRequest request);
    
    List<JobApplicationDTO> getMyApplications(Long userId);
    
    JobApplicationDTO getApplicationById(Long userId, Long applicationId);
    
    // Stats for Dashboard
    JobStatsDTO getJobStats(Long userId);
    
    // Get saved/applied job IDs for quick lookup in frontend
    List<Long> getSavedJobIds(Long userId);
    
    List<Long> getAppliedJobIds(Long userId);
}
