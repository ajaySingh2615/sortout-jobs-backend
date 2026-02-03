package com.cadt.sortoutjobbackend.usermanagement.dto;

import com.cadt.sortoutjobbackend.usermanagement.entity.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * DTO for admin user detail view - includes related data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDetailDTO {
    // Core user data
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String role;
    private AuthProvider authProvider;
    private String profilePicture;
    private Boolean emailVerified;
    private Boolean isActive;
    private Instant createdAt;

    // Activity counts
    private Long applicationsCount;
    private Long savedJobsCount;
    private Integer activeSessionsCount;

    // Related data
    private List<UserApplicationDTO> recentApplications;
    private List<SessionDTO> activeSessions;
    private UserProfileSummaryDTO profileSummary;

    /**
     * Nested DTO for user's job applications
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserApplicationDTO {
        private Long applicationId;
        private Long jobId;
        private String jobTitle;
        private String company;
        private String status;
        private Instant appliedAt;
    }

    /**
     * Nested DTO for user's profile summary
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileSummaryDTO {
        private String fullName;
        private String gender;
        private String educationLevel;
        private Boolean hasExperience;
        private String experienceLevel;
        private String preferredCity;
        private String preferredRole;
        private Boolean profileCompleted;
    }
}
