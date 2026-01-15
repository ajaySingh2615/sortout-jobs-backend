package com.cadt.sortoutjobbackend.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullProfileResponse {
    // Basic Info (from User + UserProfile)
    private Long userId;
    private String phone;
    private String email;
    private String fullName;
    private String profilePicture;
    private String gender;
    private String educationLevel;
    private Boolean hasExperience;
    private String experienceLevel;
    private Integer currentSalary;
    
    // Location (from UserPreferences)
    private String cityName;
    private String localityName;
    
    // Profile Module Data
    private String resumeUrl;
    private String resumeFileName;
    private String resumeHeadline;
    private String profileSummary;
    
    private List<EmploymentDTO> employments;
    private List<EducationDTO> educations;
    private List<ProjectDTO> projects;
    private List<ITSkillDTO> itSkills;
    
    private PersonalDetailsDTO personalDetails;
}
