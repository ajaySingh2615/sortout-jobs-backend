package com.cadt.sortoutjobbackend.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailChangeResponse {
    private String accessToken;
    private String newEmail;
}
