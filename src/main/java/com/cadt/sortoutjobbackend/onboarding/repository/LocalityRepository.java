package com.cadt.sortoutjobbackend.onboarding.repository;

import com.cadt.sortoutjobbackend.onboarding.entity.Locality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalityRepository extends JpaRepository<Locality, Long> {
    List<Locality> findByCityIdAndIsActiveTrue(Long cityId);
}
