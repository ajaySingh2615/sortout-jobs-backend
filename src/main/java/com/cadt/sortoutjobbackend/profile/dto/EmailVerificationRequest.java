package com.cadt.sortoutjobbackend.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailVerificationRequest {
    @NotBlank(message = "OTP is required")
    private String otp;
}
