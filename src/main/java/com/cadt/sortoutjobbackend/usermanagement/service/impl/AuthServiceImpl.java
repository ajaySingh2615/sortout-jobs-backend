package com.cadt.sortoutjobbackend.usermanagement.service.impl;

import com.cadt.sortoutjobbackend.common.exception.ApiException;
import com.cadt.sortoutjobbackend.common.exception.ErrorCode;
import com.cadt.sortoutjobbackend.common.security.JwtTokenProvider;
import com.cadt.sortoutjobbackend.usermanagement.dto.LoginRequest;
import com.cadt.sortoutjobbackend.usermanagement.dto.LoginResponse;
import com.cadt.sortoutjobbackend.usermanagement.dto.TokenRefreshRequest;
import com.cadt.sortoutjobbackend.usermanagement.dto.TokenRefreshResponse;
import com.cadt.sortoutjobbackend.usermanagement.dto.UserDTO;
import com.cadt.sortoutjobbackend.usermanagement.dto.UserRegistrationRequest;
import com.cadt.sortoutjobbackend.usermanagement.entity.RefreshToken;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import com.cadt.sortoutjobbackend.usermanagement.service.AuthService;
import com.cadt.sortoutjobbackend.usermanagement.service.RefreshTokenService;
import com.cadt.sortoutjobbackend.usermanagement.service.UserEmailService;
import com.cadt.sortoutjobbackend.usermanagement.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserEmailService userEmailService;

    public AuthServiceImpl(UserService userService, UserRepository userRepository,
            PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider,
            RefreshTokenService refreshTokenService, UserEmailService userEmailService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.userEmailService = userEmailService;
    }

    @Override
    public LoginResponse register(UserRegistrationRequest request) {
        UserDTO userDTO = userService.createUser(request);

        // Send verification email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        userEmailService.sendVerificationEmail(user);

        String accessToken = jwtTokenProvider.generateToken(request.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .email(userDTO.getEmail())
                .role(userDTO.getRole())
                .userId(user.getId())
                .isNewUser(true)
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.AUTH_INVALID_CREDENTIALS));

        // Check if user registered with OAuth/Phone - they can't use password login
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new ApiException(ErrorCode.AUTH_USE_OAUTH);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        String accessToken = jwtTokenProvider.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .role(user.getRole())
                .userId(user.getId())
                .isNewUser(false)
                .build();
    }

    @Override
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_NOT_FOUND));

        refreshTokenService.verifyExpiration(refreshToken);

        String newAccessToken = jwtTokenProvider.generateToken(refreshToken.getUser().getEmail());

        return new TokenRefreshResponse(newAccessToken, requestRefreshToken);
    }

    @Override
    public void logout(Long userId) {
        refreshTokenService.deleteByUserId(userId);
    }
}
