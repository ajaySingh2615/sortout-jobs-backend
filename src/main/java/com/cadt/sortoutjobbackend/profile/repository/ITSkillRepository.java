package com.cadt.sortoutjobbackend.profile.repository;

import com.cadt.sortoutjobbackend.profile.entity.ITSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITSkillRepository extends JpaRepository<ITSkill, Long> {
    List<ITSkill> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
