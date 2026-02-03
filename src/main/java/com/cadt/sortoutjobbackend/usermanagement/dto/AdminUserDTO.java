package com.cadt.sortoutjobbackend.usermanagement.dto;

import com.cadt.sortoutjobbackend.usermanagement.entity.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for admin user list view - comprehensive user data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDTO {
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
}
