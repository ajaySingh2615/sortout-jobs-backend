package com.cadt.sortoutjobbackend.job.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDTO {
    private long totalJobs;
    private long activeJobs;
    private long totalApplications;
    private long pendingApplications;
    private long totalUsers;
    private long newJobsThisWeek;
    private long newApplicationsThisWeek;
}
