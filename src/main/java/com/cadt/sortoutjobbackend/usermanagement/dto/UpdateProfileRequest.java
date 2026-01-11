package com.cadt.sortoutjobbackend.usermanagement.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String profilePicture;
}
