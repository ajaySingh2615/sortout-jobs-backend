package com.cadt.sortoutjobbackend.usermanagement.controller;

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
    public ResponseEntity<UserDTO> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/{userId}/link-phone/send-otp")
    public ResponseEntity<String> sendPhoneLinkOtp(
            @PathVariable Long userId,
            @RequestBody PhoneSendOtpRequest request
    ) {
        userService.sendPhoneLinkOtp(userId, request.getPhone());
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/{userId}/link-phone/verify")
    public ResponseEntity<String> linkPhone(
            @PathVariable Long userId,
            @Valid @RequestBody LinkPhoneRequest request) {
        userService.linkPhone(userId, request);
        return ResponseEntity.ok("Phone linked successfully");
    }
}
