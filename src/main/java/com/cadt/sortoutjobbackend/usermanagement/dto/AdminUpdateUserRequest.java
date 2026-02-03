package com.cadt.sortoutjobbackend.usermanagement.dto;

import lombok.Data;

/**
 * Request DTO for admin updating a user's details
 * All fields are optional - only provided fields are updated
 */
@Data
public class AdminUpdateUserRequest {
    private String name;
    private String email;
    private String phone;
    private String role;  // JOB_SEEKER, RECRUITER, ADMIN
    private Boolean emailVerified;
    private Boolean isActive;
}
