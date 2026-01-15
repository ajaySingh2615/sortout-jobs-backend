package com.cadt.sortoutjobbackend.profile.entity;

import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "it_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ITSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;   // "Java", "React", "AWS"

    @Enumerated(EnumType.STRING)
    private Proficiency proficiency;  // BEGINNER, INTERMEDIATE, EXPERT

    private Integer experienceMonths;   // 24 = 2 years

    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
