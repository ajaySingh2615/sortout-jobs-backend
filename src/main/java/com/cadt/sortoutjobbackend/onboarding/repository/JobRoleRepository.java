package com.cadt.sortoutjobbackend.onboarding.repository;

import com.cadt.sortoutjobbackend.onboarding.entity.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRoleRepository extends JpaRepository<JobRole, Long> {
    List<JobRole> findByIsActiveTrue();
}
