package com.cadt.sortoutjobbackend.job.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyJobRequest {
    private String coverLetter;  // Optional cover letter or note
    private String resumeUrl;    // Optional - use existing resume if not provided
}
