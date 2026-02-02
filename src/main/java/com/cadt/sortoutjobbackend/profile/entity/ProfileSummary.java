package com.cadt.sortoutjobbackend.profile.entity;

import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "profile_summaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 2000)
    private String summary;  // "Experienced software engineer with 5+ years..."

    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate(){
        updatedAt = Instant.now();
    }

}
