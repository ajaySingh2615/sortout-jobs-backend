package com.cadt.sortoutjobbackend.usermanagement.service;

import com.cadt.sortoutjobbackend.usermanagement.entity.RefreshToken;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(String email);

    Optional<RefreshToken> findByToken(String token);

    RefreshToken verifyExpiration(RefreshToken token);

    void deleteByUserId(Long userId);

    // logout specific session
    void deleteByToken(String token);

    // logout all sessions for ser
    void deleteAllByUserId(Long userId);

    // Get all active sessions for user
    List<RefreshToken> getActiveSessionsByUserId(Long userId);
}
