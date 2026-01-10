package com.cadt.sortoutjobbackend.usermanagement.controller;

import com.cadt.sortoutjobbackend.common.dto.ApiResponse;
import com.cadt.sortoutjobbackend.usermanagement.dto.*;
import com.cadt.sortoutjobbackend.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
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
