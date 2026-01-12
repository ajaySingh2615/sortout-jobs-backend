package com.cadt.sortoutjobbackend.onboarding.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "job_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;  // e.g., "Software Developer", "Sales Executive"

    @Column(nullable = false)
    private String category;  // e.g., "IT", "Sales", "Data Entry"

    @Column(nullable = false)
    private Boolean isActive = true;
}
