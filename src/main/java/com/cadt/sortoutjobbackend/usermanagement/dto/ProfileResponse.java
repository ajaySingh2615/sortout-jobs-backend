package com.cadt.sortoutjobbackend.usermanagement.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProfileResponse {
    // User basic info
    private Long userId;
    private String phone;
    private String email;
    private String name;
    private String profilePicture;

    // Profile info
    private String fullName;
    private String gender;
    private String educationLevel;
    private Boolean hasExperience;
    private String experienceLevel;
    private Integer currentSalary;
    private Boolean whatsappUpdates;
    private Boolean profileCompleted;

    // Location preferences
    private Long cityId;
    private String cityName;
    private Long localityId;
    private String localityName;

    // Job preferences
    private Long roleId;
    private String roleName;
    private List<SkillInfo> skills;

    @Data
    @Builder
    public static class SkillInfo {
        private Long id;
        private String name;
    }
}
