package com.cadt.sortoutjobbackend.onboarding.service.impl;

import com.cadt.sortoutjobbackend.common.exception.ApiException;
import com.cadt.sortoutjobbackend.common.exception.ErrorCode;
import com.cadt.sortoutjobbackend.onboarding.dto.OnboardingStatusResponse;
import com.cadt.sortoutjobbackend.onboarding.dto.PreferencesRequest;
import com.cadt.sortoutjobbackend.onboarding.dto.ProfileRequest;
import com.cadt.sortoutjobbackend.onboarding.entity.*;
import com.cadt.sortoutjobbackend.onboarding.repository.*;
import com.cadt.sortoutjobbackend.onboarding.service.OnboardingService;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class OnboardingServiceImpl implements OnboardingService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final CityRepository cityRepository;
    private final LocalityRepository localityRepository;
    private final JobRoleRepository jobRoleRepository;
    private final SkillRepository skillRepository;

    public OnboardingServiceImpl(UserRepository userRepository,
                                 UserProfileRepository userProfileRepository,
                                 UserPreferencesRepository userPreferencesRepository,
                                 CityRepository cityRepository,
                                 LocalityRepository localityRepository,
                                 JobRoleRepository jobRoleRepository,
                                 SkillRepository skillRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userPreferencesRepository = userPreferencesRepository;
        this.cityRepository = cityRepository;
        this.localityRepository = localityRepository;
        this.jobRoleRepository = jobRoleRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public OnboardingStatusResponse getOnboardingStatus(Long userId) {
        boolean profileCompleted = userProfileRepository.existsByUserId(userId);
        boolean preferencesCompleted = userPreferencesRepository.findByUserId(userId)
                .map(p -> p.getPreferredRole() != null)
                .orElse(false);

        return OnboardingStatusResponse.builder()
                .profileCompleted(profileCompleted)
                .preferencesCompleted(preferencesCompleted)
                .onboardingComplete(profileCompleted && preferencesCompleted)
                .build();
    }

    @Override
    @Transactional
    public void saveProfile(Long userId, ProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        City city = cityRepository.findById(request.getPreferredCityId())
                .orElseThrow(() -> new ApiException(ErrorCode.VALIDATION_ERROR, "Invalid city"));

        Locality locality = localityRepository.findById(request.getPreferredLocalityId())
                .orElseThrow(() -> new ApiException(ErrorCode.VALIDATION_ERROR, "Invalid locality"));

        if (request.getHasExperience() && request.getExperienceLevel() == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Experience level is required");
        }

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(new UserProfile());

        profile.setUser(user);
        profile.setFullName(request.getFullName());
        profile.setGender(request.getGender());
        profile.setEducationLevel(request.getEducationLevel());
        profile.setHasExperience(request.getHasExperience());
        profile.setExperienceLevel(request.getExperienceLevel());
        profile.setCurrentSalary(request.getCurrentSalary());
        profile.setWhatsappUpdates(request.getWhatsappUpdates());
        profile.setProfileCompleted(true);

        userProfileRepository.save(profile);

        UserPreferences preferences = userPreferencesRepository.findByUserId(userId)
                .orElse(new UserPreferences());
        preferences.setUser(user);
        preferences.setPreferredCity(city);
        preferences.setPreferredLocality(locality);
        userPreferencesRepository.save(preferences);
    }

    @Override
    @Transactional
    public void savePreferences(Long userId, PreferencesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        JobRole role = jobRoleRepository.findById(request.getPreferredRoleId())
                .orElseThrow(() -> new ApiException(ErrorCode.VALIDATION_ERROR, "Invalid role"));

        Set<Skill> skills = new HashSet<>(skillRepository.findAllById(request.getSkillIds()));

        UserPreferences preferences = userPreferencesRepository.findByUserId(userId)
                .orElse(new UserPreferences());

        preferences.setUser(user);
        preferences.setPreferredRole(role);
        preferences.setSkills(skills);

        userPreferencesRepository.save(preferences);
    }
}