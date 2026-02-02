package com.cadt.sortoutjobbackend.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PhoneSendOtpRequest {

    @NotBlank(message = "Phone number is required")
    private String phone;
}
