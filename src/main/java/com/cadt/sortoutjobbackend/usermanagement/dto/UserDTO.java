package com.cadt.sortoutjobbackend.usermanagement.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String role;
    // No password field here
}
