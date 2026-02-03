package com.cadt.sortoutjobbackend.job.dto;

import com.cadt.sortoutjobbackend.job.entity.LocationType;
import com.cadt.sortoutjobbackend.profile.entity.EmploymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchRequest {
    private String keyword;           // Search in title, company, description
    private Long cityId;              // Filter by city
    private Long roleId;              // Filter by job role
    private LocationType locationType; // ONSITE, REMOTE, HYBRID
    private EmploymentType employmentType; // FULL_TIME, PART_TIME, etc.
    private Integer experienceYears;  // User's experience for matching
    private Integer salaryMin;        // Minimum expected salary
    private Integer page;             // Page number (0-based)
    private Integer size;             // Page size (default 10)
}
