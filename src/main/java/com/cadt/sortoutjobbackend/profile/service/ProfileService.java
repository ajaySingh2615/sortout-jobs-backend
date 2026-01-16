package com.cadt.sortoutjobbackend.profile.service;

import com.cadt.sortoutjobbackend.profile.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProfileService {

    // Full Profile
    FullProfileResponse getFullProfile(Long userId);

    // Resume
    void uploadResume(Long userId, MultipartFile file);
    void deleteResume(Long userId);

    // Resume Headline
    void updateResumeHeadline(Long userId, String headline);

    // Profile Summary
    void updateProfileSummary(Long userId, String summary);

    // Employment CRUD
    List<EmploymentDTO> getEmployments(Long userId);
    EmploymentDTO addEmployment(Long userId, EmploymentDTO dto);
    EmploymentDTO updateEmployment(Long userId, Long employmentId, EmploymentDTO dto);
    void deleteEmployment(Long userId, Long employmentId);

    // Education CRUD
    List<EducationDTO> getEducations(Long userId);
    EducationDTO addEducation(Long userId, EducationDTO dto);
    EducationDTO updateEducation(Long userId, Long educationId, EducationDTO dto);
    void deleteEducation(Long userId, Long educationId);

    // Project CRUD
    List<ProjectDTO> getProjects(Long userId);
    ProjectDTO addProject(Long userId, ProjectDTO dto);
    ProjectDTO updateProject(Long userId, Long projectId, ProjectDTO dto);
    void deleteProject(Long userId, Long projectId);

    // IT Skills CRUD
    List<ITSkillDTO> getITSkills(Long userId);
    ITSkillDTO addITSkill(Long userId, ITSkillDTO dto);
    ITSkillDTO updateITSkill(Long userId, Long skillId, ITSkillDTO dto);
    void deleteITSkill(Long userId, Long skillId);

    // Personal Details
    PersonalDetailsDTO getPersonalDetails(Long userId);
    void updatePersonalDetails(Long userId, PersonalDetailsDTO dto);

    // Basic Profile & Email
    void updateBasicProfile(Long userId, BasicProfileRequest request);
    void initiateEmailChange(Long userId, String newEmail);
    void verifyEmailChange(Long userId, String otp);
}
