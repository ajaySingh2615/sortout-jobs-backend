package com.cadt.sortoutjobbackend.job.config;

import com.cadt.sortoutjobbackend.job.repository.JobApplicationRepository;
import com.cadt.sortoutjobbackend.job.repository.JobRepository;
import com.cadt.sortoutjobbackend.job.service.AdminJobService;
import com.cadt.sortoutjobbackend.job.service.impl.AdminJobServiceImpl;
import com.cadt.sortoutjobbackend.onboarding.repository.CityRepository;
import com.cadt.sortoutjobbackend.onboarding.repository.JobRoleRepository;
import com.cadt.sortoutjobbackend.onboarding.repository.SkillRepository;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminJobServiceConfig {

    @Bean
    public AdminJobService adminJobService(
            JobRepository jobRepository,
            JobApplicationRepository jobApplicationRepository,
            UserRepository userRepository,
            CityRepository cityRepository,
            JobRoleRepository jobRoleRepository,
            SkillRepository skillRepository) {
        return new AdminJobServiceImpl(
                jobRepository,
                jobApplicationRepository,
                userRepository,
                cityRepository,
                jobRoleRepository,
                skillRepository);
    }
}
