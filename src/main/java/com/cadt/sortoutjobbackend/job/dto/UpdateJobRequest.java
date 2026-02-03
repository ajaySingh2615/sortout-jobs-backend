package com.cadt.sortoutjobbackend.job.dto;

import com.cadt.sortoutjobbackend.job.entity.LocationType;
import com.cadt.sortoutjobbackend.onboarding.entity.EducationLevel;
import com.cadt.sortoutjobbackend.profile.entity.EmploymentType;
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
public class UpdateJobRequest {
    
    private String title;
    private String company;
    private String companyLogo;
    private String description;
    private String requirements;
    
    // Location
    private Long cityId;
    private String address;
    private LocationType locationType;
    
    // Salary
    private Integer salaryMin;
    private Integer salaryMax;
    private Boolean isSalaryDisclosed;
    
    // Employment
    private EmploymentType employmentType;
    
    // Experience
    private Integer experienceMinYears;
    private Integer experienceMaxYears;
    
    // Education
    private EducationLevel minEducation;
    
    // Role & Skills
    private Long roleId;
    private Set<Long> skillIds;
    
    // Other
    private Integer vacancies;
    private LocalDate applicationDeadline;
    private Boolean isFeatured;
    private Boolean isActive;
}
