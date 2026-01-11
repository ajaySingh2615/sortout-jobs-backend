package com.cadt.sortoutjobbackend.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LinkPhoneRequest {

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "OTP is required")
    private String otp;
}
