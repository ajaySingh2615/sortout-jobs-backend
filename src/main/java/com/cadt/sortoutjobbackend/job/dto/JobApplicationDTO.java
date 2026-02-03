package com.cadt.sortoutjobbackend.job.dto;

import com.cadt.sortoutjobbackend.job.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationDTO {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String company;
    private String companyLogo;
    private ApplicationStatus status;
    private String resumeUrl;
    private String coverLetter;
    private Instant appliedAt;
    private Instant statusChangedAt;
    
    // Include minimal job info for display
    private JobSummary job;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobSummary {
        private Long id;
        private String title;
        private String company;
        private String companyLogo;
        private String cityName;
        private String locationType;
        private String employmentType;
        private Boolean isActive;
    }
}
