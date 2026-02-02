package com.cadt.sortoutjobbackend.onboarding.controller;

import com.cadt.sortoutjobbackend.common.dto.ApiResponse;
import com.cadt.sortoutjobbackend.onboarding.dto.OnboardingStatusResponse;
import com.cadt.sortoutjobbackend.onboarding.dto.PreferencesRequest;
import com.cadt.sortoutjobbackend.onboarding.dto.ProfileRequest;
import com.cadt.sortoutjobbackend.onboarding.service.OnboardingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onboarding")
public class OnboardingController {

    private final OnboardingService onboardingService;

    public OnboardingController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<ApiResponse<OnboardingStatusResponse>> getStatus(@PathVariable Long userId) {
        OnboardingStatusResponse status = onboardingService.getOnboardingStatus(userId);
        return ResponseEntity.ok(ApiResponse.success("Onboarding status retrieved", status));
    }

    @PostMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<Void>> saveProfile(
            @PathVariable Long userId,
            @Valid @RequestBody ProfileRequest request) {
        onboardingService.saveProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile saved successfully"));
    }

    @PostMapping("/preferences/{userId}")
    public ResponseEntity<ApiResponse<Void>> savePreferences(
            @PathVariable Long userId,
            @Valid @RequestBody PreferencesRequest request) {
        onboardingService.savePreferences(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Preferences saved successfully"));
    }
}
