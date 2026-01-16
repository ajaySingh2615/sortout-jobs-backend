package com.cadt.sortoutjobbackend.onboarding.controller;

import com.cadt.sortoutjobbackend.common.dto.ApiResponse;
import com.cadt.sortoutjobbackend.onboarding.entity.City;
import com.cadt.sortoutjobbackend.onboarding.entity.JobRole;
import com.cadt.sortoutjobbackend.onboarding.entity.Locality;
import com.cadt.sortoutjobbackend.onboarding.entity.Skill;
import com.cadt.sortoutjobbackend.onboarding.repository.CityRepository;
import com.cadt.sortoutjobbackend.onboarding.repository.JobRoleRepository;
import com.cadt.sortoutjobbackend.onboarding.repository.LocalityRepository;
import com.cadt.sortoutjobbackend.onboarding.repository.SkillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/master")
public class MasterDataController {

    private final CityRepository cityRepository;
    private final LocalityRepository localityRepository;
    private final JobRoleRepository jobRoleRepository;
    private final SkillRepository skillRepository;

    public MasterDataController(CityRepository cityRepository, LocalityRepository localityRepository,
            JobRoleRepository jobRoleRepository, SkillRepository skillRepository) {
        this.cityRepository = cityRepository;
        this.localityRepository = localityRepository;
        this.jobRoleRepository = jobRoleRepository;
        this.skillRepository = skillRepository;
    }

    @GetMapping("/cities")
    public ResponseEntity<ApiResponse<List<City>>> getAllCities() {
        List<City> cities = cityRepository.findByIsActiveTrue();
        return ResponseEntity.ok(ApiResponse.success("Cities fetched", cities));
    }

    @GetMapping("/cities/{cityId}/localities")
    public ResponseEntity<ApiResponse<List<Locality>>> getLocalitiesByCity(@PathVariable Long cityId) {
        List<Locality> localities = localityRepository.findByCityIdAndIsActiveTrue(cityId);
        return ResponseEntity.ok(ApiResponse.success("Localities fetched", localities));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<JobRole>>> getAllRoles() {
        List<JobRole> roles = jobRoleRepository.findByIsActiveTrue();
        return ResponseEntity.ok(ApiResponse.success("Roles fetched", roles));
    }

    @GetMapping("/roles/{roleId}/skills")
    public ResponseEntity<ApiResponse<List<Skill>>> getSkillsByRole(@PathVariable Long roleId) {
        List<Skill> skills = skillRepository.findByRoleIdAndIsActiveTrue(roleId);
        return ResponseEntity.ok(ApiResponse.success("Skills fetched", skills));
    }
}
