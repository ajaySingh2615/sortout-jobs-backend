package com.cadt.sortoutjobbackend.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingStatusResponse {

    private Boolean profileCompleted;
    private Boolean preferencesCompleted;
    private Boolean onboardingComplete;
}
