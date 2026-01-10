package com.cadt.sortoutjobbackend.usermanagement.controller;

import com.cadt.sortoutjobbackend.usermanagement.dto.*;
import com.cadt.sortoutjobbackend.usermanagement.entity.RefreshToken;
import com.cadt.sortoutjobbackend.usermanagement.service.AuthService;
import com.cadt.sortoutjobbackend.usermanagement.service.PhoneAuthService;
import com.cadt.sortoutjobbackend.usermanagement.service.RefreshTokenService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final PhoneAuthService phoneAuthService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, PhoneAuthService phoneAuthService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.phoneAuthService = phoneAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    // Logout current session (by refresh token)
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody TokenRefreshRequest request) {
        refreshTokenService.deleteByToken(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }

    // Logout all devices
    @PostMapping("/logout-all/{userId}")
    public ResponseEntity<String> logoutAllDevices(@PathVariable Long userId) {
        refreshTokenService.deleteAllByUserId(userId);
        return ResponseEntity.ok("All devices logged out successfully");
    }

    // Get active sessions
    @GetMapping("/sessions/{userId}")
    public ResponseEntity<List<SessionDTO>> getActiveSessions(@PathVariable Long userId) {
        List<RefreshToken> tokens = refreshTokenService.getActiveSessionsByUserId(userId);
        List<SessionDTO> sessions = tokens.stream()
                .map(t -> new SessionDTO(
                        t.getId(),
                        t.getToken().substring(0, 8) + "...",
                        t.getCreatedAt(),
                        t.getExpiryDate(),
                        false))
                .toList();

        return ResponseEntity.ok(sessions);
    }

    // send otp
    @PostMapping("/phone/send-otp")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody PhoneSendOtpRequest request) {
        phoneAuthService.sendOtp(request.getPhone());
        return ResponseEntity.ok("OTP send successfully");
    }

    // verify otp
    @PostMapping("/phone/verify-otp")
    public ResponseEntity<LoginResponse> verifyOtp(@Valid @RequestBody PhoneVerifyOtpRequest request) {
        return ResponseEntity.ok(phoneAuthService.verifyOtpAndLogin(request.getPhone(), request.getOtp()));
    }

}
