package com.cadt.sortoutjobbackend.usermanagement.service;

import com.cadt.sortoutjobbackend.usermanagement.entity.User;

/**
 * User-specific email verification service
 */
public interface UserEmailService {
    void sendVerificationEmail(User user);
    void sendWelcomeEmail(User user);
    boolean verifyEmail(String token);
    
    // Password reset
    void sendPasswordResetEmail(User user);
    void resetPassword(String token, String newPassword);
}
