package com.cadt.sortoutjobbackend.job.entity;

import com.cadt.sortoutjobbackend.onboarding.entity.City;
import com.cadt.sortoutjobbackend.onboarding.entity.EducationLevel;
import com.cadt.sortoutjobbackend.onboarding.entity.JobRole;
import com.cadt.sortoutjobbackend.onboarding.entity.Skill;
import com.cadt.sortoutjobbackend.profile.entity.EmploymentType;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;  // e.g., "Senior React Developer"

    @Column(nullable = false)
    private String company;  // e.g., "TechCorp India"

    private String companyLogo;  // URL to company logo (optional)

    @Column(columnDefinition = "TEXT")
    private String description;  // Full job description with responsibilities

    @Column(columnDefinition = "TEXT")
    private String requirements;  // Job requirements/qualifications

    // Location details
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    private String address;  // Specific address or area (optional)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType locationType = LocationType.ONSITE;

    // Salary details (stored in annual LPA * 100000 for precision, e.g., 15 LPA = 1500000)
    private Integer salaryMin;  // e.g., 1500000 (15 LPA)
    private Integer salaryMax;  // e.g., 2500000 (25 LPA)
    private Boolean isSalaryDisclosed = true;

    // Employment type
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType = EmploymentType.FULL_TIME;

    // Experience requirements (in years)
    private Integer experienceMinYears = 0;  // 0 for freshers
    private Integer experienceMaxYears;

    // Education requirement
    @Enumerated(EnumType.STRING)
    private EducationLevel minEducation;

    // Job role category (for matching with user preferences)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private JobRole role;

    // Required skills (for matching)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "job_skills",
        joinColumns = @JoinColumn(name = "job_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> requiredSkills = new HashSet<>();

    // Vacancy and application details
    private Integer vacancies = 1;
    private Integer applicationsCount = 0;

    // Dates
    private LocalDate applicationDeadline;
    private Instant postedAt;

    // Status
    @Column(nullable = false)
    private Boolean isActive = true;

    private Boolean isFeatured = false;  // For premium/highlighted jobs

    // Posted by (Admin or Employer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (postedAt == null) {
            postedAt = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
