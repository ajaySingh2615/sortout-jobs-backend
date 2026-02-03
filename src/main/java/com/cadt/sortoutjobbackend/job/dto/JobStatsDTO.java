package com.cadt.sortoutjobbackend.job.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobStatsDTO {
    private long newJobsToday;
    private long totalActiveJobs;
    private long applicationsSent;
    private long savedJobs;
    private long interviewCalls;  // Applications with status INTERVIEW_SCHEDULED
}
