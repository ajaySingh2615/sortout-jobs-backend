package com.cadt.sortoutjobbackend.profile.service.impl;

import com.cadt.sortoutjobbackend.common.exception.ApiException;
import com.cadt.sortoutjobbackend.common.exception.ErrorCode;
import com.cadt.sortoutjobbackend.common.security.JwtTokenProvider;
import com.cadt.sortoutjobbackend.onboarding.entity.*;
import com.cadt.sortoutjobbackend.onboarding.repository.*;
import com.cadt.sortoutjobbackend.profile.dto.*;
import java.time.Instant;
import com.cadt.sortoutjobbackend.profile.entity.*;
import com.cadt.sortoutjobbackend.profile.mapper.ProfileMapper;
import com.cadt.sortoutjobbackend.profile.repository.*;
import com.cadt.sortoutjobbackend.profile.service.ProfileService;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final CityRepository cityRepository;
    private final LocalityRepository localityRepository;
    private final com.cadt.sortoutjobbackend.common.service.EmailService emailService;
    private final ResumeRepository resumeRepository;
    private final ResumeHeadlineRepository resumeHeadlineRepository;
    private final ProfileSummaryRepository profileSummaryRepository;
    private final EmploymentRepository employmentRepository;
    private final EducationRepository educationRepository;
    private final ProjectRepository projectRepository;
    private final ITSkillRepository itSkillRepository;
    private final PersonalDetailsRepository personalDetailsRepository;
    private final ProfileMapper profileMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(readOnly = true)
    public FullProfileResponse getFullProfile(Long userId) {
        User user = findUserById(userId);

        FullProfileResponse.FullProfileResponseBuilder builder = FullProfileResponse.builder()
                .userId(user.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .emailVerified(user.getEmailVerified())
                .profilePicture(user.getProfilePicture());

        // UserProfile (onboarding)
        userProfileRepository.findByUserId(userId).ifPresent(profile -> {
            builder.fullName(profile.getFullName())
                    .gender(profile.getGender() != null ? profile.getGender().name() : null)
                    .educationLevel(profile.getEducationLevel() != null ? profile.getEducationLevel().name() : null)
                    .hasExperience(profile.getHasExperience())
                    .experienceLevel(profile.getExperienceLevel() != null ? profile.getExperienceLevel().name() : null)
                    .currentSalary(profile.getCurrentSalary())
                    .noticePeriod(profile.getNoticePeriod());
        });

        // UserPreferences (onboarding)
        userPreferencesRepository.findByUserIdWithDetails(userId).ifPresent(prefs -> {
            if (prefs.getPreferredCity() != null) {
                builder.cityId(prefs.getPreferredCity().getId());
                builder.cityName(prefs.getPreferredCity().getName());
            }
            if (prefs.getPreferredLocality() != null) {
                builder.localityId(prefs.getPreferredLocality().getId());
                builder.localityName(prefs.getPreferredLocality().getName());
            }
        });

        // Resume
        resumeRepository.findByUserId(userId).ifPresent(resume -> {
            builder.resumeUrl(resume.getFileUrl())
                    .resumeFileName(resume.getFileName());
        });

        // Resume Headline
        resumeHeadlineRepository.findByUserId(userId).ifPresent(headline -> {
            builder.resumeHeadline(headline.getHeadline());
        });

        // Profile Summary
        profileSummaryRepository.findByUserId(userId).ifPresent(summary -> {
            builder.profileSummary(summary.getSummary());
        });

        // Lists
        builder.employments(getEmployments(userId));
        builder.educations(getEducations(userId));
        builder.projects(getProjects(userId));
        builder.itSkills(getITSkills(userId));
        builder.personalDetails(getPersonalDetails(userId));

        return builder.build();
    }

    // ==================== RESUME ====================

    @Override
    @Transactional
    public void uploadResume(Long userId, MultipartFile file) {
        User user = findUserById(userId);

        // TODO: Upload to S3/local storage and get URL
        String fileUrl = "/uploads/resumes/" + userId + "_" + file.getOriginalFilename();

        Resume resume = resumeRepository.findByUserId(userId)
                .orElse(new Resume());

        resume.setUser(user);
        resume.setFileUrl(fileUrl);
        resume.setFileName(file.getOriginalFilename());
        resume.setFileType(file.getContentType());
        resume.setFileSize(file.getSize());

        resumeRepository.save(resume);
    }

    @Override
    @Transactional
    public void deleteResume(Long userId) {
        resumeRepository.deleteByUserId(userId);
    }

    // ==================== RESUME HEADLINE ====================

    @Override
    @Transactional
    public void updateResumeHeadline(Long userId, String headline) {
        User user = findUserById(userId);

        ResumeHeadline entity = resumeHeadlineRepository.findByUserId(userId)
                .orElse(new ResumeHeadline());

        entity.setUser(user);
        entity.setHeadline(headline);
        resumeHeadlineRepository.save(entity);
    }

    // ==================== PROFILE SUMMARY ====================

    @Override
    @Transactional
    public void updateProfileSummary(Long userId, String summary) {
        User user = findUserById(userId);

        ProfileSummary entity = profileSummaryRepository.findByUserId(userId)
                .orElse(new ProfileSummary());

        entity.setUser(user);
        entity.setSummary(summary);
        profileSummaryRepository.save(entity);
    }

    // ==================== EMPLOYMENT CRUD ====================

    @Override
    public List<EmploymentDTO> getEmployments(Long userId) {
        return employmentRepository.findByUserIdOrderByStartDateDesc(userId).stream()
                .map(profileMapper::toEmploymentDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmploymentDTO addEmployment(Long userId, EmploymentDTO dto) {
        User user = findUserById(userId);
        Employment entity = profileMapper.toEmployment(dto);
        entity.setUser(user);
        return profileMapper.toEmploymentDTO(employmentRepository.save(entity));
    }

    @Override
    @Transactional
    public EmploymentDTO updateEmployment(Long userId, Long employmentId, EmploymentDTO dto) {
        Employment entity = employmentRepository.findById(employmentId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
        validateOwnership(entity.getUser().getId(), userId);
        profileMapper.updateEmployment(entity, dto);
        return profileMapper.toEmploymentDTO(employmentRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteEmployment(Long userId, Long employmentId) {
        Employment entity = employmentRepository.findById(employmentId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
        validateOwnership(entity.getUser().getId(), userId);
        employmentRepository.delete(entity);
    }

    // ==================== EDUCATION CRUD ====================

    @Override
    public List<EducationDTO> getEducations(Long userId) {
        return educationRepository.findByUserIdOrderByPassOutYearDesc(userId).stream()
                .map(profileMapper::toEducationDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EducationDTO addEducation(Long userId, EducationDTO dto) {
        User user = findUserById(userId);
        Education entity = profileMapper.toEducation(dto);
        entity.setUser(user);
        return profileMapper.toEducationDTO(educationRepository.save(entity));
    }

    @Override
    @Transactional
    public EducationDTO updateEducation(Long userId, Long educationId, EducationDTO dto) {
        Education entity = educationRepository.findById(educationId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
        validateOwnership(entity.getUser().getId(), userId);
        profileMapper.updateEducation(entity, dto);
        return profileMapper.toEducationDTO(educationRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteEducation(Long userId, Long educationId) {
        Education entity = educationRepository.findById(educationId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
        validateOwnership(entity.getUser().getId(), userId);
        educationRepository.delete(entity);
    }

    // ==================== PROJECT CRUD ====================

    @Override
    public List<ProjectDTO> getProjects(Long userId) {
        return projectRepository.findByUserIdOrderByStartDateDesc(userId).stream()
                .map(profileMapper::toProjectDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDTO addProject(Long userId, ProjectDTO dto) {
        User user = findUserById(userId);
        Project entity = profileMapper.toProject(dto);
        entity.setUser(user);
        return profileMapper.toProjectDTO(projectRepository.save(entity));
    }

    @Override
    @Transactional
    public ProjectDTO updateProject(Long userId, Long projectId, ProjectDTO dto) {
        Project entity = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
        validateOwnership(entity.getUser().getId(), userId);
        profileMapper.updateProject(entity, dto);
        return profileMapper.toProjectDTO(projectRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteProject(Long userId, Long projectId) {
        Project entity = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
        validateOwnership(entity.getUser().getId(), userId);
        projectRepository.delete(entity);
    }

    // ==================== IT SKILLS CRUD ====================

    @Override
    public List<ITSkillDTO> getITSkills(Long userId) {
        return itSkillRepository.findByUserId(userId).stream()
                .map(profileMapper::toITSkillDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ITSkillDTO addITSkill(Long userId, ITSkillDTO dto) {
        User user = findUserById(userId);
        ITSkill entity = profileMapper.toITSkill(dto);
        entity.setUser(user);
        return profileMapper.toITSkillDTO(itSkillRepository.save(entity));
    }

    @Override
    @Transactional
    public ITSkillDTO updateITSkill(Long userId, Long skillId, ITSkillDTO dto) {
        ITSkill entity = itSkillRepository.findById(skillId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
        validateOwnership(entity.getUser().getId(), userId);
        profileMapper.updateITSkill(entity, dto);
        return profileMapper.toITSkillDTO(itSkillRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteITSkill(Long userId, Long skillId) {
        ITSkill entity = itSkillRepository.findById(skillId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
        validateOwnership(entity.getUser().getId(), userId);
        itSkillRepository.delete(entity);
    }

    // ==================== PERSONAL DETAILS ====================

    @Override
    public PersonalDetailsDTO getPersonalDetails(Long userId) {
        return personalDetailsRepository.findByUserId(userId)
                .map(profileMapper::toPersonalDetailsDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public void updatePersonalDetails(Long userId, PersonalDetailsDTO dto) {
        User user = findUserById(userId);
        PersonalDetails entity = personalDetailsRepository.findByUserId(userId)
                .orElse(new PersonalDetails());
        entity.setUser(user);
        profileMapper.updatePersonalDetails(entity, dto);
        personalDetailsRepository.save(entity);
    }

    // ==================== BASIC PROFILE & EMAIL ====================

    @Override
    @Transactional
    public void updateBasicProfile(Long userId, BasicProfileRequest request) {
        User user = findUserById(userId);

        // 1. Update User Profile (FullName, Experience, Salary, Notice Period)
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElse(new UserProfile());

        userProfile.setUser(user);
        userProfile.setFullName(request.getFullName());
        userProfile.setHasExperience(request.getHasExperience());

        // Handle Experience Level with validation
        // If hasExperience is false, clear all experience-related fields
        if (Boolean.FALSE.equals(request.getHasExperience())) {
            userProfile.setExperienceLevel(null);
            userProfile.setCurrentSalary(null);
            userProfile.setNoticePeriod(null);
        } else if (request.getExperienceLevel() != null && !request.getExperienceLevel().isEmpty()) {
            // Only set experience level if hasExperience is true
            try {
                userProfile.setExperienceLevel(ExperienceLevel.valueOf(request.getExperienceLevel()));
            } catch (IllegalArgumentException e) {
                throw new ApiException(ErrorCode.VALIDATION_ERROR,
                        "Invalid experience level: " + request.getExperienceLevel());
            }
        } else {
            userProfile.setExperienceLevel(null);
        }

        // Only set salary if hasExperience is true
        if (Boolean.TRUE.equals(request.getHasExperience()) && request.getCurrentSalary() != null) {
            userProfile.setCurrentSalary(request.getCurrentSalary());
        } else {
            userProfile.setCurrentSalary(null);
        }

        // Only set notice period if hasExperience is true
        if (Boolean.TRUE.equals(request.getHasExperience())
                && request.getNoticePeriod() != null && !request.getNoticePeriod().isEmpty()) {
            userProfile.setNoticePeriod(request.getNoticePeriod());
        } else {
            userProfile.setNoticePeriod(null);
        }

        userProfileRepository.save(userProfile);

        // Update Headline if provided
        if (request.getHeadline() != null && !request.getHeadline().trim().isEmpty()) {
            updateResumeHeadline(userId, request.getHeadline());
        }

        // Update user name in User entity as well if needed (optional but good for
        // consistency)
        user.setName(request.getFullName());
        userRepository.save(user);

        // 2. Update User Preferences (Location)
        if (request.getCityId() != null && request.getLocalityId() != null) {
            City city = cityRepository.findById(request.getCityId())
                    .orElseThrow(() -> new ApiException(ErrorCode.VALIDATION_ERROR, "Invalid city"));

            Locality locality = localityRepository.findById(request.getLocalityId())
                    .orElseThrow(() -> new ApiException(ErrorCode.VALIDATION_ERROR, "Invalid locality"));

            UserPreferences preferences = userPreferencesRepository.findByUserId(userId)
                    .orElse(new UserPreferences());

            preferences.setUser(user);
            preferences.setPreferredCity(city);
            preferences.setPreferredLocality(locality);

            userPreferencesRepository.save(preferences);
        }
    }

    // Field definition removed from here as it should be at the top level

    @Override
    @Transactional
    public EmailChangeInitiateResponse initiateEmailChange(Long userId, String newEmail) {
        User user = findUserById(userId);

        if (userRepository.existsByEmail(newEmail)) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Email already in use");
        }

        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        Instant expiryTime = Instant.now().plusSeconds(300); // 5 mins (industry standard)

        user.setPendingEmail(newEmail);
        user.setEmailChangeOtp(otp);
        user.setEmailChangeOtpExpiry(expiryTime);

        userRepository.save(user);

        // Log OTP for development/testing (remove in production)
        System.out.println("========================================");
        System.out.println("EMAIL CHANGE OTP FOR: " + newEmail);
        System.out.println("OTP: " + otp);
        System.out.println("Expires at: " + expiryTime);
        System.out.println("========================================");

        try {
            emailService.sendEmail(newEmail, "Verify Email Change - SortOut Jobs",
                    "Your OTP for email change is: " + otp + "\n\nThis code will expire in 5 minutes.");
        } catch (Exception e) {
            // Log error but don't fail - OTP is already saved
            System.err.println("Failed to send email, but OTP is saved: " + e.getMessage());
            e.printStackTrace();
            // In production, you might want to throw here or use a retry mechanism
            // For now, we'll let it continue so user can still use the OTP from logs
        }

        long expiresInSeconds = java.time.Duration.between(Instant.now(), expiryTime).getSeconds();
        return new EmailChangeInitiateResponse(
                "OTP sent successfully to " + newEmail,
                expiryTime,
                expiresInSeconds);
    }

    @Override
    @Transactional
    public EmailChangeResponse verifyEmailChange(Long userId, String otp) {
        User user = findUserById(userId);

        if (user.getPendingEmail() == null || user.getEmailChangeOtp() == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "No pending email change request");
        }

        // Check expiry FIRST (security best practice)
        if (user.getEmailChangeOtpExpiry() == null ||
                user.getEmailChangeOtpExpiry().isBefore(java.time.Instant.now())) {
            // Clear expired OTP
            user.setPendingEmail(null);
            user.setEmailChangeOtp(null);
            user.setEmailChangeOtpExpiry(null);
            userRepository.save(user);
            throw new ApiException(ErrorCode.OTP_EXPIRED, "OTP has expired. Please request a new one.");
        }

        // Then check OTP match
        if (!user.getEmailChangeOtp().equals(otp)) {
            throw new ApiException(ErrorCode.OTP_INVALID, "Invalid OTP");
        }

        String newEmail = user.getPendingEmail();
        user.setEmail(newEmail);
        user.setPendingEmail(null);
        user.setEmailChangeOtp(null);
        user.setEmailChangeOtpExpiry(null);
        // Reset email verified if strict, but usually we verify the new email via OTP
        // so it is verified
        user.setEmailVerified(true);

        userRepository.save(user);

        // Generate new access token with updated email
        String newAccessToken = jwtTokenProvider.generateToken(newEmail);

        return new EmailChangeResponse(newAccessToken, newEmail);
    }

    // ==================== HELPER METHODS ====================

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateOwnership(Long ownerId, Long requesterId) {
        if (!ownerId.equals(requesterId)) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
    }
}
