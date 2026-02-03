package com.cadt.sortoutjobbackend.usermanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role; // e.g., "JOB_SEEKER", "RECRUITER", "ADMIN"

    private AuthProvider authProvider = AuthProvider.LOCAL;

    private String providerId;  // Google's user ID

    private String name;   // From Google profile

    private String profilePicture;  // From Google profile

    @Column(unique = true)
    private String phone;

    @Column
    private Boolean emailVerified = false;

    @Column
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    // Email Change Process
    private String pendingEmail;
    private String emailChangeOtp;
    private Instant emailChangeOtpExpiry;
}
