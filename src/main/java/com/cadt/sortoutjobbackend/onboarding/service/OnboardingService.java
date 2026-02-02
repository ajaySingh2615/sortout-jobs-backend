package com.cadt.sortoutjobbackend.onboarding.service;

import com.cadt.sortoutjobbackend.onboarding.dto.OnboardingStatusResponse;
import com.cadt.sortoutjobbackend.onboarding.dto.PreferencesRequest;
import com.cadt.sortoutjobbackend.onboarding.dto.ProfileRequest;

public interface OnboardingService {
    OnboardingStatusResponse getOnboardingStatus(Long userId);

    void saveProfile(Long userId, ProfileRequest request);

    void savePreferences(Long userId, PreferencesRequest request);
}