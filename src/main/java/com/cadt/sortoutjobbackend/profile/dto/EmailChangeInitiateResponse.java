package com.cadt.sortoutjobbackend.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailChangeInitiateResponse {
    private String message;
    private Instant otpExpiresAt; // When OTP expires
    private long expiresInSeconds; // Seconds until expiry
}
