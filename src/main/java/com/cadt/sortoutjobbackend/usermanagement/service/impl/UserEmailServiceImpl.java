package com.cadt.sortoutjobbackend.usermanagement.service.impl;

import com.cadt.sortoutjobbackend.common.service.EmailService;
import com.cadt.sortoutjobbackend.usermanagement.entity.EmailVerificationToken;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.repository.EmailVerificationTokenRepository;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import com.cadt.sortoutjobbackend.usermanagement.service.UserEmailService;
import org.springframework.beans.factory.annotation.Value;
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
    private final UserRepository userRepository;

    public UserEmailServiceImpl(EmailService emailService,
                                 EmailVerificationTokenRepository tokenRepository,
                                 UserRepository userRepository) {
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void sendVerificationEmail(User user) {
        // Delete existing tokens for this user
        tokenRepository.deleteByUserId(user.getId());

        // Create new token
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryTime(Instant.now().plusSeconds(24 * 60 * 60)); // 24 hours
        tokenRepository.save(verificationToken);

        // Create verification link
        String verificationLink = baseUrl + "/api/auth/verify-email?token=" + token;

        // Email content
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
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (verificationToken.isUsed()) {
            throw new RuntimeException("Token already used");
        }

        if (verificationToken.getExpiryTime().isBefore(Instant.now())) {
            throw new RuntimeException("Token expired");
        }

        // Mark token as used
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        // Mark user as verified
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        // Send welcome email
        sendWelcomeEmail(user);

        return true;
    }
}
