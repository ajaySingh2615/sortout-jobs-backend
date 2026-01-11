package com.cadt.sortoutjobbackend.usermanagement.controller;

import com.cadt.sortoutjobbackend.common.dto.ApiResponse;
import com.cadt.sortoutjobbackend.common.exception.ApiException;
import com.cadt.sortoutjobbackend.common.exception.ErrorCode;
import com.cadt.sortoutjobbackend.common.exception.RateLimitException;
import com.cadt.sortoutjobbackend.common.ratelimit.RateLimiterService;
import com.cadt.sortoutjobbackend.usermanagement.dto.*;
import com.cadt.sortoutjobbackend.usermanagement.entity.RefreshToken;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import com.cadt.sortoutjobbackend.usermanagement.service.AuthService;
import com.cadt.sortoutjobbackend.usermanagement.service.PhoneAuthService;
import com.cadt.sortoutjobbackend.usermanagement.service.RefreshTokenService;
import com.cadt.sortoutjobbackend.usermanagement.service.UserEmailService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final PhoneAuthService phoneAuthService;
    private final UserEmailService userEmailService;
    private final UserRepository userRepository;
    private final RateLimiterService rateLimiter;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService,
                          PhoneAuthService phoneAuthService, UserEmailService userEmailService,
                          UserRepository userRepository, RateLimiterService rateLimiter) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.phoneAuthService = phoneAuthService;
        this.userEmailService = userEmailService;
        this.userRepository = userRepository;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody UserRegistrationRequest request) {
        String rateLimitKey = "register:" + request.getEmail();
        if (!rateLimiter.isAllowed(rateLimitKey, 3, 3600)) {
            int retryAfter = (int) rateLimiter.getSecondsUntilReset(rateLimitKey, 3600);
            throw new RateLimitException("Too many registration attempts. Try again later.", retryAfter);
        }

        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful. Please verify your email.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        String rateLimitKey = "login:" + request.getEmail();
        if (!rateLimiter.isAllowed(rateLimitKey, 5, 900)) {
            int retryAfter = (int) rateLimiter.getSecondsUntilReset(rateLimitKey, 900);
            throw new RateLimitException(
                    "Too many login attempts. Try again in " + (retryAfter / 60) + " minutes.",
                    retryAfter
            );
        }

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody TokenRefreshRequest request) {
        refreshTokenService.deleteByToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @PostMapping("/logout-all/{userId}")
    public ResponseEntity<ApiResponse<Void>> logoutAllDevices(@PathVariable Long userId) {
        refreshTokenService.deleteAllByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("All devices logged out successfully"));
    }

    @GetMapping("/sessions/{userId}")
    public ResponseEntity<ApiResponse<List<SessionDTO>>> getActiveSessions(@PathVariable Long userId) {
        List<RefreshToken> tokens = refreshTokenService.getActiveSessionsByUserId(userId);
        List<SessionDTO> sessions = tokens.stream()
                .map(t -> new SessionDTO(
                        t.getId(),
                        t.getToken().substring(0, 8) + "...",
                        t.getCreatedAt(),
                        t.getExpiryDate(),
                        false))
                .toList();

        return ResponseEntity.ok(ApiResponse.success("Active sessions retrieved", sessions));
    }

    @PostMapping("/phone/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@Valid @RequestBody PhoneSendOtpRequest request) {
        String rateLimitKey = "otp:" + request.getPhone();
        if (!rateLimiter.isAllowed(rateLimitKey, 3, 3600)) {
            int retryAfter = (int) rateLimiter.getSecondsUntilReset(rateLimitKey, 3600);
            throw new RateLimitException("Too many OTP requests. Try again later.", retryAfter);
        }

        phoneAuthService.sendOtp(request.getPhone());
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully"));
    }

    @PostMapping("/phone/verify-otp")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyOtp(@Valid @RequestBody PhoneVerifyOtpRequest request) {
        LoginResponse response = phoneAuthService.verifyOtpAndLogin(request.getPhone(), request.getOtp());
        return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", response));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        userEmailService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully! Welcome to SortOut Jobs!"));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam String email) {
        String rateLimitKey = "resend:" + email;
        if (!rateLimiter.isAllowed(rateLimitKey, 3, 3600)) {
            int retryAfter = (int) rateLimiter.getSecondsUntilReset(rateLimitKey, 3600);
            throw new RateLimitException("Too many requests. Try again later.", retryAfter);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new ApiException(ErrorCode.AUTH_ALREADY_VERIFIED);
        }

        userEmailService.sendVerificationEmail(user);
        return ResponseEntity.ok(ApiResponse.success("Verification email sent"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        String rateLimitKey = "forgot:" + email;
        if (!rateLimiter.isAllowed(rateLimitKey, 3, 3600)) {
            int retryAfter = (int) rateLimiter.getSecondsUntilReset(rateLimitKey, 3600);
            throw new RateLimitException("Too many requests. Try again later.", retryAfter);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        userEmailService.sendPasswordResetEmail(user);
        return ResponseEntity.ok(ApiResponse.success("Password reset email sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userEmailService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully. You can now login with your new password."));
    }
}
