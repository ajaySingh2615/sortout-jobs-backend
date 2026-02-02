package com.cadt.sortoutjobbackend.profile.entity;

import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "employments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String designation;  // software engineer
    private String company;       // "DNS Technologies Pvt Ltd"

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;   // FULL_TIME, PART_TIME, INTERNSHIP, FREELANCE

    private LocalDate startDate;
    private LocalDate endDate;   // null if current job
    private Boolean isCurrent = false;

    private String noticePeriod;  // "30 days", "Immediate", "2 months"

    @Column(length = 2000)
    private String description;  // Job responsibilities

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
