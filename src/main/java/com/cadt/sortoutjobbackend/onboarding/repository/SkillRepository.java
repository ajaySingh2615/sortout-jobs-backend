package com.cadt.sortoutjobbackend.onboarding.repository;

import com.cadt.sortoutjobbackend.onboarding.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByRoleIdAndIsActiveTrue(Long roleId);
}
