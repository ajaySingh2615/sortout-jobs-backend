package com.cadt.sortoutjobbackend.usermanagement.dto;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String email;
    private String password;
    private String role;
}
