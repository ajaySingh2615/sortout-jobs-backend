package com.cadt.sortoutjobbackend.usermanagement.service.impl;

import com.cadt.sortoutjobbackend.common.exception.ApiException;
import com.cadt.sortoutjobbackend.common.exception.ErrorCode;
import com.cadt.sortoutjobbackend.common.security.JwtTokenProvider;
import com.cadt.sortoutjobbackend.usermanagement.dto.LoginResponse;
import com.cadt.sortoutjobbackend.usermanagement.entity.AuthProvider;
import com.cadt.sortoutjobbackend.usermanagement.entity.Otp;
import com.cadt.sortoutjobbackend.usermanagement.entity.RefreshToken;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.repository.OtpRepository;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import com.cadt.sortoutjobbackend.usermanagement.service.PhoneAuthService;
import com.cadt.sortoutjobbackend.usermanagement.service.RefreshTokenService;
import com.cadt.sortoutjobbackend.usermanagement.service.SmsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Random;

@Service
public class PhoneAuthServiceImpl implements PhoneAuthService {

    private static final int OTP_EXPIRY_MINUTES = 5;

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final SmsService smsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public PhoneAuthServiceImpl(OtpRepository otpRepository, UserRepository userRepository,
                                SmsService smsService, JwtTokenProvider jwtTokenProvider,
                                RefreshTokenService refreshTokenService) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.smsService = smsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    @Transactional
    public void sendOtp(String phone) {
        // Delete any existing OTP for this phone
        otpRepository.deleteByPhone(phone);

        // Generate 6-digit OTP
        String otpCode = String.format("%06d", new Random().nextInt(999999));

        // Save OTP to database
        Otp otp = new Otp();
        otp.setPhone(phone);
        otp.setOtpCode(otpCode);
        otp.setExpiryTime(Instant.now().plusSeconds(OTP_EXPIRY_MINUTES * 60));
        otpRepository.save(otp);

        // Send sms
        smsService.sendOtp(phone, otpCode);
    }

    @Override
    @Transactional
    public LoginResponse verifyOtpAndLogin(String phone, String otp) {
        // Find otp
        Otp otpEntity = otpRepository.findByPhoneAndVerifiedFalse(phone)
                .orElseThrow(() -> new ApiException(ErrorCode.OTP_NOT_FOUND));

        // check expiry
        if (otpEntity.getExpiryTime().isBefore(Instant.now())) {
            otpRepository.delete(otpEntity);
            throw new ApiException(ErrorCode.OTP_EXPIRED);
        }

        // verify otp
        if (!otpEntity.getOtpCode().equals(otp)) {
            throw new ApiException(ErrorCode.OTP_INVALID);
        }

        // Mark as verified and delete
        otpRepository.delete(otpEntity);

        // Find or create user - track if new
        final boolean[] isNewUser = {false};
        User user = userRepository.findByPhone(phone)
                .orElseGet(() -> {
                    isNewUser[0] = true;
                    User newUser = new User();
                    newUser.setPhone(phone);
                    newUser.setEmail(phone + "@phone.local");  // placeholder email
                    newUser.setPassword("");
                    newUser.setAuthProvider(AuthProvider.PHONE);
                    newUser.setRole("JOB_SEEKER");
                    return userRepository.save(newUser);
                });

        // Check if existing user account is disabled
        if (!isNewUser[0] && Boolean.FALSE.equals(user.getIsActive())) {
            throw new ApiException(ErrorCode.AUTH_ACCOUNT_DISABLED);
        }

        // Generate tokens
        String accessToken = jwtTokenProvider.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .role(user.getRole())
                .userId(user.getId())
                .isNewUser(isNewUser[0])
                .build();
    }
}
