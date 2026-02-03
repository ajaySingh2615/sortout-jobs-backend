package com.cadt.sortoutjobbackend.job.entity;

import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "job_applications", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "job_id"})  // User can apply only once per job
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    // Snapshot of user's resume at time of application (URL)
    private String resumeUrl;

    // Cover letter or additional note from applicant
    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    // Recruiter/Admin notes (internal)
    @Column(columnDefinition = "TEXT")
    private String recruiterNotes;

    // When status was last changed
    private Instant statusChangedAt;

    // Timestamps
    private Instant appliedAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        appliedAt = Instant.now();
        updatedAt = Instant.now();
        statusChangedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
