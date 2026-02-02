package com.cadt.sortoutjobbackend.profile.dto;

import com.cadt.sortoutjobbackend.onboarding.entity.ExperienceLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BasicProfileRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    private Long cityId;
    private Long localityId;

    private Boolean hasExperience;
    private String experienceLevel;

    @Min(value = 0, message = "Salary cannot be negative")
    private Integer currentSalary;

    private String noticePeriod;
    private String headline;
}
