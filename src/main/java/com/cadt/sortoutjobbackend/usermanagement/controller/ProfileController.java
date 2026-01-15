package com.cadt.sortoutjobbackend.usermanagement.controller;

import com.cadt.sortoutjobbackend.common.dto.ApiResponse;
import com.cadt.sortoutjobbackend.profile.dto.FullProfileResponse;
import com.cadt.sortoutjobbackend.profile.service.ProfileService;
import com.cadt.sortoutjobbackend.usermanagement.dto.*;
import com.cadt.sortoutjobbackend.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;
    private final ProfileService profileService;

    public ProfileController(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<FullProfileResponse>> getProfile(@PathVariable Long userId) {
        FullProfileResponse response = profileService.getFullProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request) {
        UserDTO response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @PostMapping("/{userId}/link-phone/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendPhoneLinkOtp(
            @PathVariable Long userId,
            @RequestBody PhoneSendOtpRequest request) {
        userService.sendPhoneLinkOtp(userId, request.getPhone());
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully"));
    }

    @PostMapping("/{userId}/link-phone/verify")
    public ResponseEntity<ApiResponse<Void>> linkPhone(
            @PathVariable Long userId,
            @Valid @RequestBody LinkPhoneRequest request) {
        userService.linkPhone(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Phone linked successfully"));
    }
}

