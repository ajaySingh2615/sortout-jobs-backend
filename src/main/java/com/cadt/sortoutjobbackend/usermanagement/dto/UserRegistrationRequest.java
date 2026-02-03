package com.cadt.sortoutjobbackend.usermanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /** Optional. Allowed: JOB_SEEKER, RECRUITER. ADMIN cannot be set via registration. Defaults to JOB_SEEKER if null/blank. */
    private String role;
}
