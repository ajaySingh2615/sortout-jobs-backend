package com.cadt.sortoutjobbackend.profile.repository;

import com.cadt.sortoutjobbackend.profile.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByUserIdOrderByPassOutYearDesc(Long userId);
    void deleteByUserId(Long userId);
}
