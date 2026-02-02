package com.cadt.sortoutjobbackend.onboarding.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class PreferencesRequest {

    @NotNull(message = "Role is required")
    private Long preferredRoleId;

    @NotEmpty(message = "Select at least one skill")
    private Set<Long> skillIds;

}
