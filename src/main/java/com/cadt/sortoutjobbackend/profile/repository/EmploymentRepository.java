package com.cadt.sortoutjobbackend.profile.repository;

import com.cadt.sortoutjobbackend.profile.entity.Employment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, Long> {
    List<Employment> findByUserIdOrderByStartDateDesc(Long userId);
    void deleteByUserId(Long userId);
}
