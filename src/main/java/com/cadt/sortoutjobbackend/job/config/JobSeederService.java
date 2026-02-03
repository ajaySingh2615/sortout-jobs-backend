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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobSeederService {

    private final JobRepository jobRepository;
    private final CityRepository cityRepository;
    private final JobRoleRepository jobRoleRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public JobSeederService(JobRepository jobRepository, CityRepository cityRepository,
                            JobRoleRepository jobRoleRepository, SkillRepository skillRepository,
                            UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.cityRepository = cityRepository;
        this.jobRoleRepository = jobRoleRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void seedJobs() {
        // Get first user as the poster (or create a system user)
        User poster = userRepository.findAll().stream().findFirst().orElse(null);
        if (poster == null) {
            System.out.println("No users found. Skipping job seeding. Create a user first.");
            return;
        }

        // Get cities
        List<City> cities = cityRepository.findAll();
        City bangalore = cities.stream().filter(c -> c.getName().equals("Bangalore")).findFirst().orElse(null);
        City mumbai = cities.stream().filter(c -> c.getName().equals("Mumbai")).findFirst().orElse(null);
        City delhi = cities.stream().filter(c -> c.getName().equals("Delhi")).findFirst().orElse(null);
        City hyderabad = cities.stream().filter(c -> c.getName().equals("Hyderabad")).findFirst().orElse(null);
        City pune = cities.stream().filter(c -> c.getName().equals("Pune")).findFirst().orElse(null);

        // Get job roles
        List<JobRole> roles = jobRoleRepository.findByIsActiveTrue();
        JobRole itRole = roles.stream().filter(r -> r.getName().equals("Software Developer")).findFirst().orElse(null);
        JobRole salesRole = roles.stream().filter(r -> r.getName().equals("Sales Executive")).findFirst().orElse(null);
        JobRole supportRole = roles.stream().filter(r -> r.getName().equals("Customer Support")).findFirst().orElse(null);
        JobRole dataEntryRole = roles.stream().filter(r -> r.getName().equals("Data Entry Operator")).findFirst().orElse(null);

        if (itRole == null) {
            System.out.println("IT Role not found. Run DataLoader first.");
            return;
        }

        // Get skills for IT - use skill IDs to avoid lazy loading issues
        List<Skill> itSkills = skillRepository.findByRoleIdAndIsActiveTrue(itRole.getId());
        
        // Create skill sets by ID for job creation
        Set<Long> reactJsSkillIds = itSkills.stream()
                .filter(s -> s.getName().equals("React") || s.getName().equals("JavaScript"))
                .map(Skill::getId)
                .collect(Collectors.toSet());
        
        Set<Long> javaSkillIds = itSkills.stream()
                .filter(s -> s.getName().equals("Java") || s.getName().equals("Spring Boot") || s.getName().equals("SQL"))
                .map(Skill::getId)
                .collect(Collectors.toSet());
        
        Set<Long> fullStackSkillIds = itSkills.stream()
                .filter(s -> s.getName().equals("React") || s.getName().equals("Java") || s.getName().equals("JavaScript"))
                .map(Skill::getId)
                .collect(Collectors.toSet());
        
        Set<Long> pythonSkillIds = itSkills.stream()
                .filter(s -> s.getName().equals("Python") || s.getName().equals("SQL"))
                .map(Skill::getId)
                .collect(Collectors.toSet());
        
        Set<Long> basicSkillIds = itSkills.stream()
                .filter(s -> s.getName().equals("Java") || s.getName().equals("JavaScript"))
                .map(Skill::getId)
                .collect(Collectors.toSet());

        // Create IT Jobs
        createJob("Senior React Developer", "TechCorp India", bangalore, LocationType.HYBRID,
                1500000, 2500000, EmploymentType.FULL_TIME, 2, 5, EducationLevel.GRADUATE,
                itRole, reactJsSkillIds, poster, true,
                "We are looking for a Senior React Developer to join our dynamic team...",
                "5+ years experience in React, Strong JavaScript fundamentals, Experience with Redux");

        createJob("Java Backend Engineer", "FinTech Solutions", mumbai, LocationType.ONSITE,
                1200000, 2200000, EmploymentType.FULL_TIME, 3, 6, EducationLevel.GRADUATE,
                itRole, javaSkillIds, poster, false,
                "Join our backend team building scalable financial applications...",
                "Strong Java skills, Spring Boot experience, Database design knowledge");

        createJob("Full Stack Developer", "Startup Hub", bangalore, LocationType.REMOTE,
                1000000, 1800000, EmploymentType.FULL_TIME, 1, 4, EducationLevel.GRADUATE,
                itRole, fullStackSkillIds, poster, true,
                "Exciting opportunity to work on cutting-edge products in a fast-paced startup...",
                "Experience with React and Node.js/Java, REST API development");

        createJob("Python Developer", "DataDriven Co", hyderabad, LocationType.HYBRID,
                800000, 1500000, EmploymentType.FULL_TIME, 0, 3, EducationLevel.GRADUATE,
                itRole, pythonSkillIds, poster, false,
                "Looking for Python developers to build data pipelines and analytics tools...",
                "Python programming, SQL knowledge, Basic understanding of data structures");

        createJob("Junior Software Developer", "CodeBase Inc", pune, LocationType.ONSITE,
                400000, 800000, EmploymentType.FULL_TIME, 0, 2, EducationLevel.GRADUATE,
                itRole, basicSkillIds, poster, false,
                "Great opportunity for freshers to kickstart their career in software development...",
                "Basic programming knowledge, Willingness to learn, Good communication skills");

        createJob("React Native Developer", "MobileFirst Apps", delhi, LocationType.REMOTE,
                1000000, 2000000, EmploymentType.FULL_TIME, 2, 5, EducationLevel.GRADUATE,
                itRole, reactJsSkillIds, poster, false,
                "Build cross-platform mobile applications using React Native...",
                "2+ years React Native experience, Published apps on App Store/Play Store preferred");

        createJob("Frontend Developer Intern", "WebWorks Studio", bangalore, LocationType.HYBRID,
                15000, 25000, EmploymentType.INTERNSHIP, 0, 0, EducationLevel.GRADUATE,
                itRole, reactJsSkillIds, poster, false,
                "3-month internship program for aspiring frontend developers...",
                "Currently pursuing or recently completed degree, Basic HTML/CSS/JS knowledge");

        // Create Sales Jobs
        if (salesRole != null) {
            List<Skill> salesSkills = skillRepository.findByRoleIdAndIsActiveTrue(salesRole.getId());
            Set<Long> salesSkillIds = salesSkills.stream().map(Skill::getId).collect(Collectors.toSet());

            createJob("Sales Executive - B2B", "Enterprise Solutions", mumbai, LocationType.ONSITE,
                    400000, 800000, EmploymentType.FULL_TIME, 1, 3, EducationLevel.GRADUATE,
                    salesRole, salesSkillIds, poster, false,
                    "Drive B2B sales for our enterprise software products...",
                    "1+ years sales experience, Good communication, Own vehicle preferred");

            createJob("Business Development Manager", "Growth Partners", delhi, LocationType.HYBRID,
                    800000, 1500000, EmploymentType.FULL_TIME, 3, 7, EducationLevel.GRADUATE,
                    salesRole, salesSkillIds, poster, true,
                    "Lead business development initiatives and build client relationships...",
                    "Proven track record in B2B sales, Team management experience");
        }

        // Create Support Jobs
        if (supportRole != null) {
            List<Skill> supportSkills = skillRepository.findByRoleIdAndIsActiveTrue(supportRole.getId());
            Set<Long> supportSkillIds = supportSkills.stream().map(Skill::getId).collect(Collectors.toSet());

            createJob("Customer Support Executive", "ServiceFirst", bangalore, LocationType.ONSITE,
                    300000, 500000, EmploymentType.FULL_TIME, 0, 2, EducationLevel.PASS_12TH,
                    supportRole, supportSkillIds, poster, false,
                    "Provide excellent customer support via phone, email, and chat...",
                    "Good communication skills, Basic computer knowledge, Rotational shifts");

            createJob("Technical Support Engineer", "TechHelp Solutions", hyderabad, LocationType.REMOTE,
                    500000, 900000, EmploymentType.FULL_TIME, 1, 4, EducationLevel.GRADUATE,
                    supportRole, supportSkillIds, poster, false,
                    "Provide technical support for our SaaS products...",
                    "Technical background, Problem-solving skills, Customer-facing experience");
        }

        // Create Data Entry Jobs
        if (dataEntryRole != null) {
            List<Skill> deSkills = skillRepository.findByRoleIdAndIsActiveTrue(dataEntryRole.getId());
            Set<Long> deSkillIds = deSkills.stream().map(Skill::getId).collect(Collectors.toSet());

            createJob("Data Entry Operator", "DataPro Services", mumbai, LocationType.ONSITE,
                    200000, 350000, EmploymentType.FULL_TIME, 0, 2, EducationLevel.PASS_12TH,
                    dataEntryRole, deSkillIds, poster, false,
                    "Accurate data entry work with attention to detail...",
                    "40+ WPM typing speed, MS Excel knowledge, Attention to detail");

            createJob("Part-time Data Entry", "FlexiWork", null, LocationType.REMOTE,
                    100000, 180000, EmploymentType.PART_TIME, 0, 1, EducationLevel.PASS_12TH,
                    dataEntryRole, deSkillIds, poster, false,
                    "Flexible work-from-home data entry opportunity...",
                    "Basic computer skills, Reliable internet connection, Own laptop");
        }

        System.out.println("Seeded " + jobRepository.count() + " jobs successfully!");
    }

    private void createJob(String title, String company, City city, LocationType locationType,
                           int salaryMin, int salaryMax, EmploymentType employmentType,
                           int expMin, int expMax, EducationLevel minEducation,
                           JobRole role, Set<Long> skillIds, User poster, boolean featured,
                           String description, String requirements) {
        // Load skills fresh to avoid lazy loading issues
        Set<Skill> skills = new HashSet<>();
        if (skillIds != null && !skillIds.isEmpty()) {
            skills = new HashSet<>(skillRepository.findAllById(skillIds));
        }

        Job job = new Job();
        job.setTitle(title);
        job.setCompany(company);
        job.setCity(city);
        job.setLocationType(locationType);
        job.setSalaryMin(salaryMin);
        job.setSalaryMax(salaryMax);
        job.setIsSalaryDisclosed(true);
        job.setEmploymentType(employmentType);
        job.setExperienceMinYears(expMin);
        job.setExperienceMaxYears(expMax);
        job.setMinEducation(minEducation);
        job.setRole(role);
        job.setRequiredSkills(skills);
        job.setPostedBy(poster);
        job.setIsFeatured(featured);
        job.setDescription(description);
        job.setRequirements(requirements);
        job.setVacancies(5);
        job.setApplicationDeadline(LocalDate.now().plusDays(30));
        job.setIsActive(true);

        jobRepository.save(job);
    }
}
