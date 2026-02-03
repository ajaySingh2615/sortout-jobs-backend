package com.cadt.sortoutjobbackend.job.service.impl;

import com.cadt.sortoutjobbackend.common.exception.ApiException;
import com.cadt.sortoutjobbackend.common.exception.ErrorCode;
import com.cadt.sortoutjobbackend.job.dto.*;
import com.cadt.sortoutjobbackend.job.entity.ApplicationStatus;
import com.cadt.sortoutjobbackend.job.entity.Job;
import com.cadt.sortoutjobbackend.job.entity.JobApplication;
import com.cadt.sortoutjobbackend.job.repository.JobApplicationRepository;
import com.cadt.sortoutjobbackend.job.repository.JobRepository;
import com.cadt.sortoutjobbackend.job.service.AdminJobService;
import com.cadt.sortoutjobbackend.onboarding.entity.City;
import com.cadt.sortoutjobbackend.onboarding.entity.JobRole;
import com.cadt.sortoutjobbackend.onboarding.entity.Skill;
import com.cadt.sortoutjobbackend.onboarding.repository.CityRepository;
import com.cadt.sortoutjobbackend.onboarding.repository.JobRoleRepository;
import com.cadt.sortoutjobbackend.onboarding.repository.SkillRepository;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AdminJobServiceImpl implements AdminJobService {

    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final JobRoleRepository jobRoleRepository;
    private final SkillRepository skillRepository;

    @Override
    @Transactional
    public JobDTO createJob(Long adminId, CreateJobRequest request) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "Admin not found"));

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setCompanyLogo(request.getCompanyLogo());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setAddress(request.getAddress());
        job.setLocationType(request.getLocationType());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setIsSalaryDisclosed(request.getIsSalaryDisclosed() != null ? request.getIsSalaryDisclosed() : true);
        job.setEmploymentType(request.getEmploymentType());
        job.setExperienceMinYears(request.getExperienceMinYears() != null ? request.getExperienceMinYears() : 0);
        job.setExperienceMaxYears(request.getExperienceMaxYears());
        job.setMinEducation(request.getMinEducation());
        job.setVacancies(request.getVacancies() != null ? request.getVacancies() : 1);
        job.setApplicationDeadline(request.getApplicationDeadline());
        job.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        job.setIsActive(true);
        job.setPostedBy(admin);

        // Set city
        if (request.getCityId() != null) {
            City city = cityRepository.findById(request.getCityId())
                    .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "City not found"));
            job.setCity(city);
        }

        // Set role
        JobRole role = jobRoleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Job role not found"));
        job.setRole(role);

        // Set skills
        if (request.getSkillIds() != null && !request.getSkillIds().isEmpty()) {
            Set<Skill> skills = new HashSet<>(skillRepository.findAllById(request.getSkillIds()));
            job.setRequiredSkills(skills);
        }

        Job saved = jobRepository.save(job);
        return toJobDTO(saved);
    }

    @Override
    @Transactional
    public JobDTO updateJob(Long jobId, UpdateJobRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Job not found"));

        if (request.getTitle() != null) job.setTitle(request.getTitle());
        if (request.getCompany() != null) job.setCompany(request.getCompany());
        if (request.getCompanyLogo() != null) job.setCompanyLogo(request.getCompanyLogo());
        if (request.getDescription() != null) job.setDescription(request.getDescription());
        if (request.getRequirements() != null) job.setRequirements(request.getRequirements());
        if (request.getAddress() != null) job.setAddress(request.getAddress());
        if (request.getLocationType() != null) job.setLocationType(request.getLocationType());
        if (request.getSalaryMin() != null) job.setSalaryMin(request.getSalaryMin());
        if (request.getSalaryMax() != null) job.setSalaryMax(request.getSalaryMax());
        if (request.getIsSalaryDisclosed() != null) job.setIsSalaryDisclosed(request.getIsSalaryDisclosed());
        if (request.getEmploymentType() != null) job.setEmploymentType(request.getEmploymentType());
        if (request.getExperienceMinYears() != null) job.setExperienceMinYears(request.getExperienceMinYears());
        if (request.getExperienceMaxYears() != null) job.setExperienceMaxYears(request.getExperienceMaxYears());
        if (request.getMinEducation() != null) job.setMinEducation(request.getMinEducation());
        if (request.getVacancies() != null) job.setVacancies(request.getVacancies());
        if (request.getApplicationDeadline() != null) job.setApplicationDeadline(request.getApplicationDeadline());
        if (request.getIsFeatured() != null) job.setIsFeatured(request.getIsFeatured());
        if (request.getIsActive() != null) job.setIsActive(request.getIsActive());

        // Update city
        if (request.getCityId() != null) {
            City city = cityRepository.findById(request.getCityId())
                    .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "City not found"));
            job.setCity(city);
        }

        // Update role
        if (request.getRoleId() != null) {
            JobRole role = jobRoleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Job role not found"));
            job.setRole(role);
        }

        // Update skills
        if (request.getSkillIds() != null) {
            Set<Skill> skills = new HashSet<>(skillRepository.findAllById(request.getSkillIds()));
            job.setRequiredSkills(skills);
        }

        Job saved = jobRepository.save(job);
        return toJobDTO(saved);
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Job not found"));
        
        // Soft delete - just mark as inactive
        job.setIsActive(false);
        jobRepository.save(job);
    }

    @Override
    @Transactional
    public void toggleJobStatus(Long jobId, boolean isActive) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Job not found"));
        job.setIsActive(isActive);
        jobRepository.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    public JobListResponse getAllJobsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postedAt"));
        Page<Job> jobPage = jobRepository.findAll(pageable);

        List<JobDTO> jobs = jobPage.getContent().stream()
                .map(this::toJobDTO)
                .collect(Collectors.toList());

        return JobListResponse.builder()
                .jobs(jobs)
                .currentPage(jobPage.getNumber())
                .totalPages(jobPage.getTotalPages())
                .totalElements(jobPage.getTotalElements())
                .pageSize(jobPage.getSize())
                .hasNext(jobPage.hasNext())
                .hasPrevious(jobPage.hasPrevious())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminStatsDTO getAdminStats() {
        Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);

        long totalJobs = jobRepository.count();
        long activeJobs = jobRepository.countByIsActiveTrue();
        long totalApplications = jobApplicationRepository.count();
        long pendingApplications = jobApplicationRepository.findAll().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING)
                .count();
        long totalUsers = userRepository.count();
        long newJobsThisWeek = jobRepository.countJobsPostedSince(weekAgo);
        long newApplicationsThisWeek = jobApplicationRepository.findAll().stream()
                .filter(a -> a.getAppliedAt() != null && a.getAppliedAt().isAfter(weekAgo))
                .count();

        return AdminStatsDTO.builder()
                .totalJobs(totalJobs)
                .activeJobs(activeJobs)
                .totalApplications(totalApplications)
                .pendingApplications(pendingApplications)
                .totalUsers(totalUsers)
                .newJobsThisWeek(newJobsThisWeek)
                .newApplicationsThisWeek(newApplicationsThisWeek)
                .build();
    }

    @Override
    @Transactional
    public void updateApplicationStatus(Long applicationId, String status, String notes) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Application not found"));

        application.setStatus(ApplicationStatus.valueOf(status));
        application.setRecruiterNotes(notes);
        application.setStatusChangedAt(Instant.now());
        jobApplicationRepository.save(application);
    }

    @Override
    @Transactional(readOnly = true)
    public JobListResponse getApplicationsForJob(Long jobId, int page, int size) {
        // This would return applications, but for now we'll just validate the job exists
        jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Job not found"));
        
        // Return empty for now - implement later with ApplicationListResponse
        return JobListResponse.builder()
                .jobs(List.of())
                .currentPage(0)
                .totalPages(0)
                .totalElements(0)
                .pageSize(size)
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }

    private JobDTO toJobDTO(Job job) {
        Set<JobDTO.SkillDTO> skillDTOs = job.getRequiredSkills().stream()
                .map(skill -> JobDTO.SkillDTO.builder()
                        .id(skill.getId())
                        .name(skill.getName())
                        .build())
                .collect(Collectors.toSet());

        return JobDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .companyLogo(job.getCompanyLogo())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .cityId(job.getCity() != null ? job.getCity().getId() : null)
                .cityName(job.getCity() != null ? job.getCity().getName() : null)
                .stateName(job.getCity() != null ? job.getCity().getState() : null)
                .address(job.getAddress())
                .locationType(job.getLocationType())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .isSalaryDisclosed(job.getIsSalaryDisclosed())
                .employmentType(job.getEmploymentType())
                .experienceMinYears(job.getExperienceMinYears())
                .experienceMaxYears(job.getExperienceMaxYears())
                .minEducation(job.getMinEducation())
                .roleId(job.getRole() != null ? job.getRole().getId() : null)
                .roleName(job.getRole() != null ? job.getRole().getName() : null)
                .roleCategory(job.getRole() != null ? job.getRole().getCategory() : null)
                .requiredSkills(skillDTOs)
                .vacancies(job.getVacancies())
                .applicationsCount(job.getApplicationsCount())
                .applicationDeadline(job.getApplicationDeadline())
                .postedAt(job.getPostedAt())
                .isActive(job.getIsActive())
                .isFeatured(job.getIsFeatured())
                .build();
    }
}
