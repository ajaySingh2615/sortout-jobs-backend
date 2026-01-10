package com.cadt.sortoutjobbackend.usermanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String role; // e.g., "JOB_SEEKER", "RECRUITER"

    private AuthProvider authProvider = AuthProvider.LOCAL;

    private String providerId;  // Google's user ID

    private String name;   // From Google profile

    private String profilePicture;  // From Google profile

    @Column(unique = true)
    private String phone;

    @Column
    private boolean emailVerified = false;

}
