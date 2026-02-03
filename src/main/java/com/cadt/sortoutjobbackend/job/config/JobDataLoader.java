package com.cadt.sortoutjobbackend.job.config;

import com.cadt.sortoutjobbackend.job.entity.Job;
import com.cadt.sortoutjobbackend.job.entity.LocationType;
import com.cadt.sortoutjobbackend.job.repository.JobRepository;
import com.cadt.sortoutjobbackend.onboarding.entity.City;
import com.cadt.sortoutjobbackend.onboarding.entity.EducationLevel;
import com.cadt.sortoutjobbackend.onboarding.entity.JobRole;
import com.cadt.sortoutjobbackend.onboarding.entity.Skill;
import com.cadt.sortoutjobbackend.onboarding.repository.CityRepository;
import com.cadt.sortoutjobbackend.onboarding.repository.JobRoleRepository;
import com.cadt.sortoutjobbackend.onboarding.repository.SkillRepository;
import com.cadt.sortoutjobbackend.profile.entity.EmploymentType;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Order(2)  // Run after DataLoader which is Order 1 by default
public class JobDataLoader implements CommandLineRunner {

    private final JobRepository jobRepository;
    private final CityRepository cityRepository;
    private final JobRoleRepository jobRoleRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final JobSeederService jobSeederService;

    public JobDataLoader(JobRepository jobRepository, CityRepository cityRepository,
                         JobRoleRepository jobRoleRepository, SkillRepository skillRepository,
                         UserRepository userRepository, JobSeederService jobSeederService) {
        this.jobRepository = jobRepository;
        this.cityRepository = cityRepository;
        this.jobRoleRepository = jobRoleRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.jobSeederService = jobSeederService;
    }

    @Override
    public void run(String... args) {
        // Only seed if no jobs exist
        if (jobRepository.count() == 0) {
            jobSeederService.seedJobs();
        }
    }
}
