package com.cadt.sortoutjobbackend.profile.controller;

import com.cadt.sortoutjobbackend.common.dto.ApiResponse;
import com.cadt.sortoutjobbackend.profile.dto.*;
import com.cadt.sortoutjobbackend.profile.service.ProfileService;
import com.cadt.sortoutjobbackend.usermanagement.dto.*;
import com.cadt.sortoutjobbackend.usermanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    // ==================== FULL PROFILE ====================

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<FullProfileResponse>> getFullProfile(@PathVariable Long userId) {
        FullProfileResponse response = profileService.getFullProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request) {
        UserDTO response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    // ==================== BASIC PROFILE & EMAIL ====================

    @PutMapping("/{userId}/basic")
    public ResponseEntity<ApiResponse<Void>> updateBasicProfile(
            @PathVariable Long userId,
            @Valid @RequestBody BasicProfileRequest request) {
        profileService.updateBasicProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Basic profile updated successfully"));
    }

    @PostMapping("/{userId}/email/initiate")
    public ResponseEntity<ApiResponse<EmailChangeInitiateResponse>> initiateEmailChange(
            @PathVariable Long userId,
            @Valid @RequestBody EmailChangeRequest request) {
        EmailChangeInitiateResponse response = profileService.initiateEmailChange(userId, request.getNewEmail());
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/{userId}/email/verify")
    public ResponseEntity<ApiResponse<EmailChangeResponse>> verifyEmailChange(
            @PathVariable Long userId,
            @Valid @RequestBody EmailVerificationRequest request) {
        EmailChangeResponse response = profileService.verifyEmailChange(userId, request.getOtp());
        return ResponseEntity.ok(ApiResponse.success("Email updated successfully", response));
    }

    // ==================== PASSWORD & PHONE ====================

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @PostMapping("/{userId}/link-phone/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendPhoneLinkOtp(
            @PathVariable Long userId,
            @RequestBody PhoneSendOtpRequest request) {
        userService.sendPhoneLinkOtp(userId, request.getPhone());
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully"));
    }

    @PostMapping("/{userId}/link-phone/verify")
    public ResponseEntity<ApiResponse<Void>> linkPhone(
            @PathVariable Long userId,
            @Valid @RequestBody LinkPhoneRequest request) {
        userService.linkPhone(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Phone linked successfully"));
    }

    // ==================== RESUME ====================

    @PostMapping(value = "/{userId}/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> uploadResume(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        profileService.uploadResume(userId, file);
        return ResponseEntity.ok(ApiResponse.success("Resume uploaded successfully"));
    }

    @DeleteMapping("/{userId}/resume")
    public ResponseEntity<ApiResponse<Void>> deleteResume(@PathVariable Long userId) {
        profileService.deleteResume(userId);
        return ResponseEntity.ok(ApiResponse.success("Resume deleted successfully"));
    }

    // ==================== RESUME HEADLINE ====================

    @PutMapping("/{userId}/headline")
    public ResponseEntity<ApiResponse<Void>> updateHeadline(
            @PathVariable Long userId,
            @RequestBody String headline) {
        profileService.updateResumeHeadline(userId, headline);
        return ResponseEntity.ok(ApiResponse.success("Headline updated successfully"));
    }

    // ==================== PROFILE SUMMARY ====================

    @PutMapping("/{userId}/summary")
    public ResponseEntity<ApiResponse<Void>> updateSummary(
            @PathVariable Long userId,
            @RequestBody String summary) {
        profileService.updateProfileSummary(userId, summary);
        return ResponseEntity.ok(ApiResponse.success("Summary updated successfully"));
    }

    // ==================== EMPLOYMENT CRUD ====================

    @GetMapping("/{userId}/employments")
    public ResponseEntity<ApiResponse<List<EmploymentDTO>>> getEmployments(@PathVariable Long userId) {
        List<EmploymentDTO> list = profileService.getEmployments(userId);
        return ResponseEntity.ok(ApiResponse.success("Employments retrieved", list));
    }

    @PostMapping("/{userId}/employments")
    public ResponseEntity<ApiResponse<EmploymentDTO>> addEmployment(
            @PathVariable Long userId,
            @RequestBody EmploymentDTO dto) {
        EmploymentDTO result = profileService.addEmployment(userId, dto);
        return ResponseEntity.ok(ApiResponse.success("Employment added", result));
    }

    @PutMapping("/{userId}/employments/{employmentId}")
    public ResponseEntity<ApiResponse<EmploymentDTO>> updateEmployment(
            @PathVariable Long userId,
            @PathVariable Long employmentId,
            @RequestBody EmploymentDTO dto) {
        EmploymentDTO result = profileService.updateEmployment(userId, employmentId, dto);
        return ResponseEntity.ok(ApiResponse.success("Employment updated", result));
    }

    @DeleteMapping("/{userId}/employments/{employmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployment(
            @PathVariable Long userId,
            @PathVariable Long employmentId) {
        profileService.deleteEmployment(userId, employmentId);
        return ResponseEntity.ok(ApiResponse.success("Employment deleted"));
    }

    // ==================== EDUCATION CRUD ====================

    @GetMapping("/{userId}/educations")
    public ResponseEntity<ApiResponse<List<EducationDTO>>> getEducations(@PathVariable Long userId) {
        List<EducationDTO> list = profileService.getEducations(userId);
        return ResponseEntity.ok(ApiResponse.success("Educations retrieved", list));
    }

    @PostMapping("/{userId}/educations")
    public ResponseEntity<ApiResponse<EducationDTO>> addEducation(
            @PathVariable Long userId,
            @RequestBody EducationDTO dto) {
        EducationDTO result = profileService.addEducation(userId, dto);
        return ResponseEntity.ok(ApiResponse.success("Education added", result));
    }

    @PutMapping("/{userId}/educations/{educationId}")
    public ResponseEntity<ApiResponse<EducationDTO>> updateEducation(
            @PathVariable Long userId,
            @PathVariable Long educationId,
            @RequestBody EducationDTO dto) {
        EducationDTO result = profileService.updateEducation(userId, educationId, dto);
        return ResponseEntity.ok(ApiResponse.success("Education updated", result));
    }

    @DeleteMapping("/{userId}/educations/{educationId}")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @PathVariable Long userId,
            @PathVariable Long educationId) {
        profileService.deleteEducation(userId, educationId);
        return ResponseEntity.ok(ApiResponse.success("Education deleted"));
    }

    // ==================== PROJECT CRUD ====================

    @GetMapping("/{userId}/projects")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getProjects(@PathVariable Long userId) {
        List<ProjectDTO> list = profileService.getProjects(userId);
        return ResponseEntity.ok(ApiResponse.success("Projects retrieved", list));
    }

    @PostMapping("/{userId}/projects")
    public ResponseEntity<ApiResponse<ProjectDTO>> addProject(
            @PathVariable Long userId,
            @RequestBody ProjectDTO dto) {
        ProjectDTO result = profileService.addProject(userId, dto);
        return ResponseEntity.ok(ApiResponse.success("Project added", result));
    }

    @PutMapping("/{userId}/projects/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(
            @PathVariable Long userId,
            @PathVariable Long projectId,
            @RequestBody ProjectDTO dto) {
        ProjectDTO result = profileService.updateProject(userId, projectId, dto);
        return ResponseEntity.ok(ApiResponse.success("Project updated", result));
    }

    @DeleteMapping("/{userId}/projects/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable Long userId,
            @PathVariable Long projectId) {
        profileService.deleteProject(userId, projectId);
        return ResponseEntity.ok(ApiResponse.success("Project deleted"));
    }

    // ==================== IT SKILLS CRUD ====================

    @GetMapping("/{userId}/it-skills")
    public ResponseEntity<ApiResponse<List<ITSkillDTO>>> getITSkills(@PathVariable Long userId) {
        List<ITSkillDTO> list = profileService.getITSkills(userId);
        return ResponseEntity.ok(ApiResponse.success("IT Skills retrieved", list));
    }

    @PostMapping("/{userId}/it-skills")
    public ResponseEntity<ApiResponse<ITSkillDTO>> addITSkill(
            @PathVariable Long userId,
            @RequestBody ITSkillDTO dto) {
        ITSkillDTO result = profileService.addITSkill(userId, dto);
        return ResponseEntity.ok(ApiResponse.success("IT Skill added", result));
    }

    @PutMapping("/{userId}/it-skills/{skillId}")
    public ResponseEntity<ApiResponse<ITSkillDTO>> updateITSkill(
            @PathVariable Long userId,
            @PathVariable Long skillId,
            @RequestBody ITSkillDTO dto) {
        ITSkillDTO result = profileService.updateITSkill(userId, skillId, dto);
        return ResponseEntity.ok(ApiResponse.success("IT Skill updated", result));
    }

    @DeleteMapping("/{userId}/it-skills/{skillId}")
    public ResponseEntity<ApiResponse<Void>> deleteITSkill(
            @PathVariable Long userId,
            @PathVariable Long skillId) {
        profileService.deleteITSkill(userId, skillId);
        return ResponseEntity.ok(ApiResponse.success("IT Skill deleted"));
    }

    // ==================== PERSONAL DETAILS ====================

    @GetMapping("/{userId}/personal-details")
    public ResponseEntity<ApiResponse<PersonalDetailsDTO>> getPersonalDetails(@PathVariable Long userId) {
        PersonalDetailsDTO result = profileService.getPersonalDetails(userId);
        return ResponseEntity.ok(ApiResponse.success("Personal details retrieved", result));
    }

    @PutMapping("/{userId}/personal-details")
    public ResponseEntity<ApiResponse<Void>> updatePersonalDetails(
            @PathVariable Long userId,
            @RequestBody PersonalDetailsDTO dto) {
        profileService.updatePersonalDetails(userId, dto);
        return ResponseEntity.ok(ApiResponse.success("Personal details updated"));
    }
}
