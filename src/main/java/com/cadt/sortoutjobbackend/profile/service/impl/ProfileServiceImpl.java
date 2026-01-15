package com.cadt.sortoutjobbackend.profile.service.impl;

import com.cadt.sortoutjobbackend.common.exception.ApiException;
import com.cadt.sortoutjobbackend.common.exception.ErrorCode;
import com.cadt.sortoutjobbackend.onboarding.repository.UserPreferencesRepository;
import com.cadt.sortoutjobbackend.onboarding.repository.UserProfileRepository;
import com.cadt.sortoutjobbackend.profile.dto.*;
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
    private final ResumeRepository resumeRepository;
    private final ResumeHeadlineRepository resumeHeadlineRepository;
    private final ProfileSummaryRepository profileSummaryRepository;
    private final EmploymentRepository employmentRepository;
    private final EducationRepository educationRepository;
    private final ProjectRepository projectRepository;
    private final ITSkillRepository itSkillRepository;
    private final PersonalDetailsRepository personalDetailsRepository;
    private final ProfileMapper profileMapper;

    @Override
    @Transactional(readOnly = true)
    public FullProfileResponse getFullProfile(Long userId) {
        User user = findUserById(userId);

        FullProfileResponse.FullProfileResponseBuilder builder = FullProfileResponse.builder()
                .userId(user.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture());

        // UserProfile (onboarding)
        userProfileRepository.findByUserId(userId).ifPresent(profile -> {
            builder.fullName(profile.getFullName())
                    .gender(profile.getGender() != null ? profile.getGender().name() : null)
                    .educationLevel(profile.getEducationLevel() != null ? profile.getEducationLevel().name() : null)
                    .hasExperience(profile.getHasExperience())
                    .experienceLevel(profile.getExperienceLevel() != null ? profile.getExperienceLevel().name() : null)
                    .currentSalary(profile.getCurrentSalary());
        });

        // UserPreferences (onboarding)
        userPreferencesRepository.findByUserIdWithDetails(userId).ifPresent(prefs -> {
            if (prefs.getPreferredCity() != null) {
                builder.cityName(prefs.getPreferredCity().getName());
            }
            if (prefs.getPreferredLocality() != null) {
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
