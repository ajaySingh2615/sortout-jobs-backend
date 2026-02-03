package com.cadt.sortoutjobbackend.job.dto;

import com.cadt.sortoutjobbackend.job.entity.LocationType;
import com.cadt.sortoutjobbackend.profile.entity.EmploymentType;
import com.cadt.sortoutjobbackend.onboarding.entity.EducationLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {
    private Long id;
    private String title;
    private String company;
    private String companyLogo;
    private String description;
    private String requirements;
    
    // Location
    private Long cityId;
    private String cityName;
    private String stateName;
    private String address;
    private LocationType locationType;
    
    // Salary
    private Integer salaryMin;
    private Integer salaryMax;
    private Boolean isSalaryDisclosed;
    
    // Employment details
    private EmploymentType employmentType;
    private Integer experienceMinYears;
    private Integer experienceMaxYears;
    private EducationLevel minEducation;
    
    // Role and Skills
    private Long roleId;
    private String roleName;
    private String roleCategory;
    private Set<SkillDTO> requiredSkills;
    
    // Vacancy info
    private Integer vacancies;
    private Integer applicationsCount;
    
    // Dates
    private LocalDate applicationDeadline;
    private Instant postedAt;
    
    // Status
    private Boolean isActive;
    private Boolean isFeatured;
    
    // User interaction status (for logged-in user)
    private Boolean isSaved;
    private Boolean isApplied;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillDTO {
        private Long id;
        private String name;
    }
}
