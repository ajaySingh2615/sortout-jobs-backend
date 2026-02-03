package com.cadt.sortoutjobbackend.common.security;

import com.cadt.sortoutjobbackend.usermanagement.entity.AuthProvider;
import com.cadt.sortoutjobbackend.usermanagement.entity.RefreshToken;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import com.cadt.sortoutjobbackend.usermanagement.service.RefreshTokenService;
import com.cadt.sortoutjobbackend.usermanagement.service.UserEmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.oauth2.redirectUri}")
    private String redirectUrl;

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserEmailService userEmailService;

    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider,
                                              UserRepository userRepository,
                                              RefreshTokenService refreshTokenService,
                                              UserEmailService userEmailService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.userEmailService = userEmailService;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        String providerId = oAuth2User.getAttribute("sub");

        // Check if this is a new user
        boolean isNewUser = userRepository.findByEmail(email).isEmpty();

        // Find existing user by email OR create new user
        User user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    // Link Google to existing account - update Google profile info
                    if (existingUser.getProviderId() == null) {
                        existingUser.setProviderId(providerId);
                    }
                    if (existingUser.getName() == null || existingUser.getName().isEmpty()) {
                        existingUser.setName(name);
                    }
                    if (existingUser.getProfilePicture() == null) {
                        existingUser.setProfilePicture(picture);
                    }
                    // Google verified email
                    existingUser.setEmailVerified(true);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // Create new user only if email doesn't exist
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setProfilePicture(picture);
                    newUser.setAuthProvider(AuthProvider.GOOGLE);
                    newUser.setProviderId(providerId);
                    newUser.setPassword("");
                    newUser.setRole("JOB_SEEKER");
                    newUser.setEmailVerified(true); // Google already verified
                    return userRepository.save(newUser);
                });

        // Check if existing user account is disabled
        if (!isNewUser && Boolean.FALSE.equals(user.getIsActive())) {
            String errorUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                    .queryParam("error", URLEncoder.encode("Your account has been disabled. Please contact support.", StandardCharsets.UTF_8))
                    .build().toUriString();
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
            return;
        }

        // Send welcome email for new users
        if (isNewUser) {
            userEmailService.sendWelcomeEmail(user);
        }

        // Generate tokens
        String accessToken = jwtTokenProvider.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        // Redirect with tokens
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken.getToken())
                .queryParam("email", user.getEmail())
                .queryParam("role", user.getRole())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

