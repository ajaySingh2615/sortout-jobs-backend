package com.cadt.sortoutjobbackend.profile.repository;

import com.cadt.sortoutjobbackend.profile.entity.PersonalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalDetailsRepository extends JpaRepository<PersonalDetails, Long> {
    Optional<PersonalDetails> findByUserId(Long userId);
}
