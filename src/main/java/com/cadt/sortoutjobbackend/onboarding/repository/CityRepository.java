package com.cadt.sortoutjobbackend.onboarding.repository;

import com.cadt.sortoutjobbackend.onboarding.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByIsActiveTrue();
}
