package com.cadt.sortoutjobbackend.job.service.impl;

import com.cadt.sortoutjobbackend.common.exception.ApiException;
import com.cadt.sortoutjobbackend.common.exception.ErrorCode;
import com.cadt.sortoutjobbackend.job.dto.*;
import com.cadt.sortoutjobbackend.job.entity.*;
import com.cadt.sortoutjobbackend.job.repository.*;
import com.cadt.sortoutjobbackend.job.service.JobService;
import com.cadt.sortoutjobbackend.onboarding.entity.Skill;
import com.cadt.sortoutjobbackend.onboarding.entity.UserPreferences;
import com.cadt.sortoutjobbackend.onboarding.repository.UserPreferencesRepository;
import com.cadt.sortoutjobbackend.profile.repository.ResumeRepository;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final SavedJobRepository savedJobRepository;
    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final ResumeRepository resumeRepository;

    @Override
    @Transactional(readOnly = true)
    public JobListResponse getAllJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobPage = jobRepository.findByIsActiveTrueOrderByPostedAtDesc(pageable);
        return toJobListResponse(jobPage, null);
    }

    @Override
    @Transactional(readOnly = true)
    public JobListResponse searchJobs(JobSearchRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size);

        Page<Job> jobPage = jobRepository.searchJobs(
            request.getKeyword(),
            request.getCityId(),
            request.getRoleId(),
            request.getLocationType(),
            request.getEmploymentType(),
            request.getExperienceYears(),
            request.getExperienceYears(),
            pageable
        );

        return toJobListResponse(jobPage, null);
    }

    @Override
    @Transactional(readOnly = true)
    public JobListResponse getRecommendedJobs(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        // Get user preferences
        UserPreferences preferences = userPreferencesRepository.findByUserIdWithDetails(userId)
            .orElse(null);

        Page<Job> jobPage;
        
        if (preferences != null && preferences.getPreferredRole() != null) {
            // Get jobs matching user's preferred role
            jobPage = jobRepository.findRecommendedByRole(preferences.getPreferredRole().getId(), pageable);
            
            // If user has skills, we could also match by skills
            if (jobPage.isEmpty() && preferences.getSkills() != null && !preferences.getSkills().isEmpty()) {
                Set<Long> skillIds = preferences.getSkills().stream()
                    .map(Skill::getId)
                    .collect(Collectors.toSet());
                jobPage = jobRepository.findBySkillIds(skillIds, pageable);
            }
        } else {
            // No preferences, return all active jobs
            jobPage = jobRepository.findByIsActiveTrueOrderByPostedAtDesc(pageable);
        }

        return toJobListResponse(jobPage, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public JobDTO getJobById(Long jobId, Long userId) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Job not found"));
        
        return toJobDTO(job, userId);
    }

    @Override
    @Transactional
    public void saveJob(Long userId, Long jobId) {
        User user = findUserById(userId);
        Job job = findJobById(jobId);

        // Check if already saved
        if (savedJobRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new ApiException(ErrorCode.RESOURCE_CONFLICT, "Job already saved");
        }

        SavedJob savedJob = new SavedJob();
        savedJob.setUser(user);
        savedJob.setJob(job);
        savedJobRepository.save(savedJob);
    }

    @Override
    @Transactional
    public void unsaveJob(Long userId, Long jobId) {
        if (!savedJobRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Saved job not found");
        }
        savedJobRepository.deleteByUserIdAndJobId(userId, jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public JobListResponse getSavedJobs(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SavedJob> savedJobPage = savedJobRepository.findByUserIdOrderBySavedAtDesc(userId, pageable);
        
        List<JobDTO> jobs = savedJobPage.getContent().stream()
            .map(savedJob -> toJobDTO(savedJob.getJob(), userId))
            .collect(Collectors.toList());

        return JobListResponse.builder()
            .jobs(jobs)
            .currentPage(savedJobPage.getNumber())
            .totalPages(savedJobPage.getTotalPages())
            .totalElements(savedJobPage.getTotalElements())
            .pageSize(savedJobPage.getSize())
            .hasNext(savedJobPage.hasNext())
            .hasPrevious(savedJobPage.hasPrevious())
            .build();
    }

    @Override
    @Transactional
    public JobApplicationDTO applyToJob(Long userId, Long jobId, ApplyJobRequest request) {
        User user = findUserById(userId);
        Job job = findJobById(jobId);

        // Check if already applied
        if (jobApplicationRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new ApiException(ErrorCode.RESOURCE_CONFLICT, "Already applied to this job");
        }

        // Check if job is still active
        if (!job.getIsActive()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "This job is no longer accepting applications");
        }

        // Get user's resume URL if not provided
        String resumeUrl = request.getResumeUrl();
        if (resumeUrl == null || resumeUrl.isBlank()) {
            resumeRepository.findByUserId(userId).ifPresent(resume -> {
                // Use existing resume
            });
        }

        JobApplication application = new JobApplication();
        application.setUser(user);
        application.setJob(job);
        application.setStatus(ApplicationStatus.PENDING);
        application.setResumeUrl(resumeUrl);
        application.setCoverLetter(request.getCoverLetter());

        JobApplication saved = jobApplicationRepository.save(application);

        // Increment application count on job
        job.setApplicationsCount(job.getApplicationsCount() + 1);
        jobRepository.save(job);

        return toJobApplicationDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationDTO> getMyApplications(Long userId) {
        return jobApplicationRepository.findByUserIdOrderByAppliedAtDesc(userId)
            .stream()
            .map(this::toJobApplicationDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JobApplicationDTO getApplicationById(Long userId, Long applicationId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Application not found"));

        // Ensure user owns this application
        if (!application.getUser().getId().equals(userId)) {
            throw new ApiException(ErrorCode.FORBIDDEN, "Not authorized to view this application");
        }

        return toJobApplicationDTO(application);
    }

    @Override
    @Transactional(readOnly = true)
    public JobStatsDTO getJobStats(Long userId) {
        Instant todayStart = Instant.now().truncatedTo(ChronoUnit.DAYS);
        
        long newJobsToday = jobRepository.countJobsPostedSince(todayStart);
        long totalActiveJobs = jobRepository.countByIsActiveTrue();
        long applicationsSent = jobApplicationRepository.countByUserId(userId);
        long savedJobs = savedJobRepository.countByUserId(userId);
        long interviewCalls = jobApplicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.INTERVIEW_SCHEDULED);

        return JobStatsDTO.builder()
            .newJobsToday(newJobsToday)
            .totalActiveJobs(totalActiveJobs)
            .applicationsSent(applicationsSent)
            .savedJobs(savedJobs)
            .interviewCalls(interviewCalls)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getSavedJobIds(Long userId) {
        return savedJobRepository.findJobIdsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getAppliedJobIds(Long userId) {
        return jobApplicationRepository.findJobIdsByUserId(userId);
    }

    // ============ Helper Methods ============

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "User not found"));
    }

    private Job findJobById(Long jobId) {
        return jobRepository.findById(jobId)
            .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Job not found"));
    }

    private JobListResponse toJobListResponse(Page<Job> jobPage, Long userId) {
        Set<Long> savedJobIds = new HashSet<>();
        Set<Long> appliedJobIds = new HashSet<>();
        
        if (userId != null) {
            savedJobIds = new HashSet<>(savedJobRepository.findJobIdsByUserId(userId));
            appliedJobIds = new HashSet<>(jobApplicationRepository.findJobIdsByUserId(userId));
        }

        final Set<Long> finalSavedIds = savedJobIds;
        final Set<Long> finalAppliedIds = appliedJobIds;

        List<JobDTO> jobs = jobPage.getContent().stream()
            .map(job -> toJobDTOWithStatus(job, finalSavedIds.contains(job.getId()), finalAppliedIds.contains(job.getId())))
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

    private JobDTO toJobDTO(Job job, Long userId) {
        boolean isSaved = false;
        boolean isApplied = false;
        
        if (userId != null) {
            isSaved = savedJobRepository.existsByUserIdAndJobId(userId, job.getId());
            isApplied = jobApplicationRepository.existsByUserIdAndJobId(userId, job.getId());
        }
        
        return toJobDTOWithStatus(job, isSaved, isApplied);
    }

    private JobDTO toJobDTOWithStatus(Job job, boolean isSaved, boolean isApplied) {
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
            .isSaved(isSaved)
            .isApplied(isApplied)
            .build();
    }

    private JobApplicationDTO toJobApplicationDTO(JobApplication application) {
        Job job = application.getJob();
        
        JobApplicationDTO.JobSummary jobSummary = JobApplicationDTO.JobSummary.builder()
            .id(job.getId())
            .title(job.getTitle())
            .company(job.getCompany())
            .companyLogo(job.getCompanyLogo())
            .cityName(job.getCity() != null ? job.getCity().getName() : null)
            .locationType(job.getLocationType() != null ? job.getLocationType().name() : null)
            .employmentType(job.getEmploymentType() != null ? job.getEmploymentType().name() : null)
            .isActive(job.getIsActive())
            .build();

        return JobApplicationDTO.builder()
            .id(application.getId())
            .jobId(job.getId())
            .jobTitle(job.getTitle())
            .company(job.getCompany())
            .companyLogo(job.getCompanyLogo())
            .status(application.getStatus())
            .resumeUrl(application.getResumeUrl())
            .coverLetter(application.getCoverLetter())
            .appliedAt(application.getAppliedAt())
            .statusChangedAt(application.getStatusChangedAt())
            .job(jobSummary)
            .build();
    }
}
