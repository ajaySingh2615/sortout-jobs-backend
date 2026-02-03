package com.cadt.sortoutjobbackend.job.dto;

import com.cadt.sortoutjobbackend.job.entity.LocationType;
import com.cadt.sortoutjobbackend.onboarding.entity.EducationLevel;
import com.cadt.sortoutjobbackend.profile.entity.EmploymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobRequest {
    
    @NotBlank(message = "Job title is required")
    private String title;
    
    @NotBlank(message = "Company name is required")
    private String company;
    
    private String companyLogo;
    
    private String description;
    
    private String requirements;
    
    // Location
    private Long cityId;
    private String address;
    
    @NotNull(message = "Location type is required")
    private LocationType locationType;
    
    // Salary
    private Integer salaryMin;
    private Integer salaryMax;
    private Boolean isSalaryDisclosed = true;
    
    // Employment
    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;
    
    // Experience
    private Integer experienceMinYears = 0;
    private Integer experienceMaxYears;
    
    // Education
    private EducationLevel minEducation;
    
    // Role & Skills
    @NotNull(message = "Job role is required")
    private Long roleId;
    
    private Set<Long> skillIds;
    
    // Other
    private Integer vacancies = 1;
    private LocalDate applicationDeadline;
    private Boolean isFeatured = false;
}
