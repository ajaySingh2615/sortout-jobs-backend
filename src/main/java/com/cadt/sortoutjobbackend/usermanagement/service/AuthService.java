package com.cadt.sortoutjobbackend.usermanagement.service;

import com.cadt.sortoutjobbackend.usermanagement.dto.*;

public interface AuthService {
    LoginResponse register(UserRegistrationRequest request);

    LoginResponse login(LoginRequest request);

    TokenRefreshResponse refreshToken(TokenRefreshRequest request);

    void logout(Long userId);
}
