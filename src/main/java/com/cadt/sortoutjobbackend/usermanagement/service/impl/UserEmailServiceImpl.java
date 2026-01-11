package com.cadt.sortoutjobbackend.usermanagement.service.impl;

import com.cadt.sortoutjobbackend.common.exception.ApiException;
import com.cadt.sortoutjobbackend.common.exception.ErrorCode;
import com.cadt.sortoutjobbackend.common.service.EmailService;
import com.cadt.sortoutjobbackend.usermanagement.entity.EmailVerificationToken;
import com.cadt.sortoutjobbackend.usermanagement.entity.PasswordResetToken;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.repository.EmailVerificationTokenRepository;
import com.cadt.sortoutjobbackend.usermanagement.repository.PasswordResetTokenRepository;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import com.cadt.sortoutjobbackend.usermanagement.service.UserEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserEmailServiceImpl implements UserEmailService {

    @Value("${app.base-url}")
    private String baseUrl;

    private final EmailService emailService;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEmailServiceImpl(EmailService emailService,
                                 EmailVerificationTokenRepository tokenRepository,
                                 PasswordResetTokenRepository resetTokenRepository,
                                 UserRepository userRepository,
                                 @Lazy PasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void sendVerificationEmail(User user) {
        tokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryTime(Instant.now().plusSeconds(24 * 60 * 60));
        tokenRepository.save(verificationToken);

        String verificationLink = baseUrl + "/api/auth/verify-email?token=" + token;

        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #333;">Welcome to SortOut Jobs!</h2>
                <p>Hi %s,</p>
                <p>Thank you for registering. Please verify your email address by clicking the button below:</p>
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="background-color: #dc2626; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; font-weight: bold;">
                        Verify Email
                    </a>
                </div>
                <p>Or copy and paste this link in your browser:</p>
                <p style="color: #666; word-break: break-all;">%s</p>
                <p>This link expires in 24 hours.</p>
                <br>
                <p>Best regards,<br>SortOut Jobs Team</p>
            </div>
            """.formatted(
                user.getName() != null ? user.getName() : "there",
                verificationLink,
                verificationLink
            );

        emailService.sendEmail(user.getEmail(), "Verify your email - SortOut Jobs", htmlContent);
    }

    @Override
    public void sendWelcomeEmail(User user) {
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #333;">🎉 Welcome to SortOut Jobs!</h2>
                <p>Hi %s,</p>
                <p>Your account has been successfully created. You're now ready to explore job opportunities!</p>
                <div style="background-color: #f3f4f6; padding: 20px; border-radius: 8px; margin: 20px 0;">
                    <h3 style="margin-top: 0;">What you can do now:</h3>
                    <ul>
                        <li>Complete your profile</li>
                        <li>Browse job listings</li>
                        <li>Apply to your dream jobs</li>
                    </ul>
                </div>
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="background-color: #dc2626; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; font-weight: bold;">
                        Go to Dashboard
                    </a>
                </div>
                <br>
                <p>Best regards,<br>SortOut Jobs Team</p>
            </div>
            """.formatted(
                user.getName() != null ? user.getName() : "there",
                baseUrl
            );

        emailService.sendEmail(user.getEmail(), "Welcome to SortOut Jobs! 🎉", htmlContent);
    }

    @Override
    @Transactional
    public boolean verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ApiException(ErrorCode.VERIFICATION_TOKEN_INVALID));

        if (verificationToken.isUsed()) {
            throw new ApiException(ErrorCode.VERIFICATION_TOKEN_USED);
        }

        if (verificationToken.getExpiryTime().isBefore(Instant.now())) {
            throw new ApiException(ErrorCode.VERIFICATION_TOKEN_EXPIRED);
        }

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        sendWelcomeEmail(user);

        return true;
    }

    @Override
    @Transactional
    public void sendPasswordResetEmail(User user) {
        // Check if user is OAuth/Phone user
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new ApiException(ErrorCode.AUTH_USE_OAUTH);
        }

        resetTokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryTime(Instant.now().plusSeconds(60 * 60)); // 1 hour
        resetTokenRepository.save(resetToken);

        // Note: In production, this would link to your frontend reset password page
        // For testing, we'll include the token directly
        String resetLink = baseUrl + "/reset-password?token=" + token;

        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #333;">Reset Your Password</h2>
                <p>Hi %s,</p>
                <p>We received a request to reset your password.</p>
                
                <div style="background-color: #f3f4f6; padding: 20px; border-radius: 8px; margin: 20px 0;">
                    <p style="margin: 0; font-size: 14px;"><strong>Your reset token:</strong></p>
                    <p style="margin: 10px 0; font-family: monospace; font-size: 12px; word-break: break-all; color: #dc2626;">%s</p>
                </div>
                
                <p><strong>To reset via API, send POST to:</strong></p>
                <p style="font-family: monospace; font-size: 12px; color: #666;">/api/auth/reset-password</p>
                <p style="font-family: monospace; font-size: 11px; color: #999;">Body: { "token": "...", "newPassword": "your-new-password" }</p>
                
                <p>This link expires in 1 hour.</p>
                <p style="color: #999; font-size: 12px;">If you didn't request this, please ignore this email.</p>
                <br>
                <p>Best regards,<br>SortOut Jobs Team</p>
            </div>
            """.formatted(
                user.getName() != null ? user.getName() : "there",
                token
            );

        emailService.sendEmail(user.getEmail(), "Reset Your Password - SortOut Jobs", htmlContent);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ApiException(ErrorCode.RESET_TOKEN_INVALID));

        if (resetToken.isUsed()) {
            throw new ApiException(ErrorCode.RESET_TOKEN_USED);
        }

        if (resetToken.getExpiryTime().isBefore(Instant.now())) {
            throw new ApiException(ErrorCode.RESET_TOKEN_EXPIRED);
        }

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
