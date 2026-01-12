package com.cadt.sortoutjobbackend.onboarding.dto;

import com.cadt.sortoutjobbackend.onboarding.entity.EducationLevel;
import com.cadt.sortoutjobbackend.onboarding.entity.ExperienceLevel;
import com.cadt.sortoutjobbackend.onboarding.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfileRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Education level is required")
    private EducationLevel educationLevel;

    @NotNull(message = "Experience status is required")
    private Boolean hasExperience;

    private ExperienceLevel experienceLevel;  // Required if hasExperience = true
    private Integer currentSalary;  // Required if hasExperience = true

    @NotNull(message = "City is required")
    private Long preferredCityId;

    @NotNull(message = "Locality is required")
    private Long preferredLocalityId;

    private Boolean whatsappUpdates = false;
}
