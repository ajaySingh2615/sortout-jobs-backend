package com.cadt.sortoutjobbackend.usermanagement.service.impl;

import com.cadt.sortoutjobbackend.common.exception.ApiException;
import com.cadt.sortoutjobbackend.common.exception.ErrorCode;
import com.cadt.sortoutjobbackend.job.entity.Job;
import com.cadt.sortoutjobbackend.job.entity.JobApplication;
import com.cadt.sortoutjobbackend.job.repository.JobApplicationRepository;
import com.cadt.sortoutjobbackend.job.repository.JobRepository;
import com.cadt.sortoutjobbackend.job.repository.SavedJobRepository;
import com.cadt.sortoutjobbackend.onboarding.entity.UserPreferences;
import com.cadt.sortoutjobbackend.onboarding.entity.UserProfile;
import com.cadt.sortoutjobbackend.onboarding.repository.UserPreferencesRepository;
import com.cadt.sortoutjobbackend.onboarding.repository.UserProfileRepository;
import com.cadt.sortoutjobbackend.usermanagement.dto.*;
import com.cadt.sortoutjobbackend.usermanagement.entity.AuthProvider;
import com.cadt.sortoutjobbackend.usermanagement.entity.RefreshToken;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.repository.RefreshTokenRepository;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import com.cadt.sortoutjobbackend.usermanagement.service.AdminUserService;
import com.cadt.sortoutjobbackend.usermanagement.service.RefreshTokenService;
import com.cadt.sortoutjobbackend.usermanagement.service.UserEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final SavedJobRepository savedJobRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserEmailService userEmailService;

    private static final Set<String> VALID_ROLES = Set.of("JOB_SEEKER", "RECRUITER", "ADMIN");

    @Override
    public AdminUserListResponse getAllUsers(
            String search,
            String role,
            Boolean isActive,
            AuthProvider authProvider,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.searchUsers(search, role, isActive, authProvider, pageable);

        List<AdminUserDTO> users = userPage.getContent().stream()
                .map(this::mapToAdminUserDTO)
                .collect(Collectors.toList());

        return AdminUserListResponse.builder()
                .users(users)
                .currentPage(userPage.getNumber())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .pageSize(userPage.getSize())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .build();
    }

    @Override
    public AdminUserStatsDTO getUserStats() {
        Instant oneWeekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        Instant oneMonthAgo = Instant.now().minus(30, ChronoUnit.DAYS);

        return AdminUserStatsDTO.builder()
                .totalUsers(userRepository.count())
                .activeUsers(userRepository.countByIsActiveTrue())
                .disabledUsers(userRepository.countByIsActiveFalse())
                .jobSeekers(userRepository.countByRole("JOB_SEEKER"))
                .recruiters(userRepository.countByRole("RECRUITER"))
                .admins(userRepository.countByRole("ADMIN"))
                .newUsersThisWeek(userRepository.countByCreatedAtAfter(oneWeekAgo))
                .newUsersThisMonth(userRepository.countByCreatedAtAfter(oneMonthAgo))
                .build();
    }

    @Override
    public AdminUserDetailDTO getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // Get activity counts
        long applicationsCount = jobApplicationRepository.countByUserId(userId);
        long savedJobsCount = savedJobRepository.countByUserId(userId);
        List<RefreshToken> sessions = refreshTokenService.getActiveSessionsByUserId(userId);

        // Get recent applications
        Pageable pageable = PageRequest.of(0, 10);
        Page<JobApplication> recentApps = jobApplicationRepository.findByUserIdOrderByAppliedAtDesc(userId, pageable);
        List<AdminUserDetailDTO.UserApplicationDTO> recentApplications = recentApps.getContent().stream()
                .map(app -> AdminUserDetailDTO.UserApplicationDTO.builder()
                        .applicationId(app.getId())
                        .jobId(app.getJob().getId())
                        .jobTitle(app.getJob().getTitle())
                        .company(app.getJob().getCompany())
                        .status(app.getStatus().name())
                        .appliedAt(app.getAppliedAt())
                        .build())
                .collect(Collectors.toList());

        // Get session DTOs
        List<SessionDTO> sessionDTOs = sessions.stream()
                .map(token -> new SessionDTO(
                        token.getId(),
                        token.getToken().substring(0, 8) + "...",
                        token.getCreatedAt(),
                        token.getExpiryDate(),
                        false
                ))
                .collect(Collectors.toList());

        // Get profile summary
        AdminUserDetailDTO.UserProfileSummaryDTO profileSummary = getProfileSummary(userId);

        return AdminUserDetailDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole())
                .authProvider(user.getAuthProvider())
                .profilePicture(user.getProfilePicture())
                .emailVerified(user.getEmailVerified())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .applicationsCount(applicationsCount)
                .savedJobsCount(savedJobsCount)
                .activeSessionsCount(sessions.size())
                .recentApplications(recentApplications)
                .activeSessions(sessionDTOs)
                .profileSummary(profileSummary)
                .build();
    }

    @Override
    @Transactional
    public AdminUserDTO updateUser(Long userId, AdminUpdateUserRequest request, Long adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // Prevent admin from demoting themselves
        if (userId.equals(adminId) && request.getRole() != null && !"ADMIN".equals(request.getRole())) {
            throw new ApiException(ErrorCode.FORBIDDEN, "Cannot demote yourself from admin");
        }

        // Update fields if provided
        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ApiException(ErrorCode.USER_EMAIL_EXISTS);
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            // Check if phone already exists
            if (request.getPhone().isBlank()) {
                user.setPhone(null);
            } else if (userRepository.existsByPhone(request.getPhone())) {
                throw new ApiException(ErrorCode.USER_PHONE_EXISTS);
            } else {
                user.setPhone(request.getPhone());
            }
        }

        if (request.getRole() != null) {
            if (!VALID_ROLES.contains(request.getRole())) {
                throw new ApiException(ErrorCode.VALIDATION_ERROR, "Invalid role: " + request.getRole());
            }
            user.setRole(request.getRole());
        }

        if (request.getEmailVerified() != null) {
            user.setEmailVerified(request.getEmailVerified());
        }

        if (request.getIsActive() != null) {
            // Prevent admin from disabling themselves
            if (userId.equals(adminId) && !request.getIsActive()) {
                throw new ApiException(ErrorCode.FORBIDDEN, "Cannot disable your own account");
            }
            user.setIsActive(request.getIsActive());
        }

        User savedUser = userRepository.save(user);
        return mapToAdminUserDTO(savedUser);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId, boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        user.setIsActive(isActive);
        userRepository.save(user);

        // If disabling user, revoke all their sessions
        if (!isActive) {
            refreshTokenService.deleteAllByUserId(userId);
        }
    }

    @Override
    @Transactional
    public void verifyUserEmail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        user.setEmailVerified(true);
        userRepository.save(user);
    }

    @Override
    public void sendPasswordReset(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // Can only reset password for LOCAL auth users
        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new ApiException(ErrorCode.USER_CANNOT_CHANGE_PASSWORD,
                    "Cannot reset password for OAuth/Phone authenticated users");
        }

        userEmailService.sendPasswordResetEmail(user);
    }

    @Override
    public List<SessionDTO> getUserSessions(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }

        List<RefreshToken> sessions = refreshTokenService.getActiveSessionsByUserId(userId);

        return sessions.stream()
                .map(token -> new SessionDTO(
                        token.getId(),
                        token.getToken().substring(0, 8) + "...",
                        token.getCreatedAt(),
                        token.getExpiryDate(),
                        false
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void revokeAllSessions(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }
        refreshTokenService.deleteAllByUserId(userId);
    }

    @Override
    @Transactional
    public void revokeSession(Long userId, Long sessionId) {
        if (!userRepository.existsById(userId)) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }

        RefreshToken token = refreshTokenRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_NOT_FOUND));

        // Verify the token belongs to the specified user
        if (!token.getUser().getId().equals(userId)) {
            throw new ApiException(ErrorCode.FORBIDDEN, "Session does not belong to this user");
        }

        refreshTokenRepository.delete(token);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId, Long adminId) {
        // Prevent admin from deleting themselves
        if (userId.equals(adminId)) {
            throw new ApiException(ErrorCode.FORBIDDEN, "Cannot delete your own account");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // Clear sessions
        refreshTokenService.deleteAllByUserId(userId);

        // Jobs posted by this user: remove applications and saved refs, then delete jobs
        List<Job> jobsPostedByUser = jobRepository.findByPostedByIdOrderByPostedAtDesc(userId, Pageable.unpaged()).getContent();
        if (!jobsPostedByUser.isEmpty()) {
            List<Long> jobIds = jobsPostedByUser.stream().map(Job::getId).collect(Collectors.toList());
            jobApplicationRepository.deleteByJob_IdIn(jobIds);
            savedJobRepository.deleteByJob_IdIn(jobIds);
            jobRepository.deleteAll(jobsPostedByUser);
        }

        // This user's applications and saved jobs
        jobApplicationRepository.deleteByUser_Id(userId);
        savedJobRepository.deleteByUser_Id(userId);

        userRepository.delete(user);
    }

    // ==================== HELPER METHODS ====================

    private AdminUserDTO mapToAdminUserDTO(User user) {
        long applicationsCount = jobApplicationRepository.countByUserId(user.getId());
        long savedJobsCount = savedJobRepository.countByUserId(user.getId());
        List<RefreshToken> sessions = refreshTokenService.getActiveSessionsByUserId(user.getId());

        return AdminUserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole())
                .authProvider(user.getAuthProvider())
                .profilePicture(user.getProfilePicture())
                .emailVerified(user.getEmailVerified())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .applicationsCount(applicationsCount)
                .savedJobsCount(savedJobsCount)
                .activeSessionsCount(sessions.size())
                .build();
    }

    private AdminUserDetailDTO.UserProfileSummaryDTO getProfileSummary(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        UserPreferences preferences = userPreferencesRepository.findByUserIdWithDetails(userId).orElse(null);

        if (profile == null && preferences == null) {
            return null;
        }

        return AdminUserDetailDTO.UserProfileSummaryDTO.builder()
                .fullName(profile != null ? profile.getFullName() : null)
                .gender(profile != null && profile.getGender() != null ? profile.getGender().name() : null)
                .educationLevel(profile != null && profile.getEducationLevel() != null ? profile.getEducationLevel().name() : null)
                .hasExperience(profile != null ? profile.getHasExperience() : null)
                .experienceLevel(profile != null && profile.getExperienceLevel() != null ? profile.getExperienceLevel().name() : null)
                .preferredCity(preferences != null && preferences.getPreferredCity() != null ? preferences.getPreferredCity().getName() : null)
                .preferredRole(preferences != null && preferences.getPreferredRole() != null ? preferences.getPreferredRole().getName() : null)
                .profileCompleted(profile != null ? profile.getProfileCompleted() : false)
                .build();
    }
}
