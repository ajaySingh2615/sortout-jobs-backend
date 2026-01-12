package com.cadt.sortoutjobbackend.onboarding.config;

import com.cadt.sortoutjobbackend.onboarding.entity.City;
import com.cadt.sortoutjobbackend.onboarding.entity.JobRole;
import com.cadt.sortoutjobbackend.onboarding.entity.Locality;
import com.cadt.sortoutjobbackend.onboarding.entity.Skill;
import com.cadt.sortoutjobbackend.onboarding.repository.CityRepository;
import com.cadt.sortoutjobbackend.onboarding.repository.JobRoleRepository;
import com.cadt.sortoutjobbackend.onboarding.repository.LocalityRepository;
import com.cadt.sortoutjobbackend.onboarding.repository.SkillRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final CityRepository cityRepository;
    private final LocalityRepository localityRepository;
    private final JobRoleRepository jobRoleRepository;
    private final SkillRepository skillRepository;

    public DataLoader(CityRepository cityRepository, LocalityRepository localityRepository,
                      JobRoleRepository jobRoleRepository, SkillRepository skillRepository) {
        this.cityRepository = cityRepository;
        this.localityRepository = localityRepository;
        this.jobRoleRepository = jobRoleRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public void run(String... args) {
        // Only seed if no data exists
        if (cityRepository.count() == 0) {
            seedCities();
        }
        if (jobRoleRepository.count() == 0) {
            seedRolesAndSkills();
        }
    }

    private void seedCities() {
        City mumbai = cityRepository.save(new City(null, "Mumbai", "Maharashtra", true));
        City delhi = cityRepository.save(new City(null, "Delhi", "Delhi", true));
        City bangalore = cityRepository.save(new City(null, "Bangalore", "Karnataka", true));
        cityRepository.save(new City(null, "Hyderabad", "Telangana", true));
        cityRepository.save(new City(null, "Chennai", "Tamil Nadu", true));
        cityRepository.save(new City(null, "Pune", "Maharashtra", true));

        // Seed localities
        localityRepository.save(new Locality(null, "Andheri", "400058", mumbai, true));
        localityRepository.save(new Locality(null, "Bandra", "400050", mumbai, true));
        localityRepository.save(new Locality(null, "Dadar", "400014", mumbai, true));
        localityRepository.save(new Locality(null, "Connaught Place", "110001", delhi, true));
        localityRepository.save(new Locality(null, "Lajpat Nagar", "110024", delhi, true));
        localityRepository.save(new Locality(null, "Koramangala", "560034", bangalore, true));
        localityRepository.save(new Locality(null, "Whitefield", "560066", bangalore, true));
    }

    private void seedRolesAndSkills() {
        // IT Role
        JobRole it = jobRoleRepository.save(new JobRole(null, "Software Developer", "IT", true));
        skillRepository.save(new Skill(null, "Java", it, true));
        skillRepository.save(new Skill(null, "JavaScript", it, true));
        skillRepository.save(new Skill(null, "Python", it, true));
        skillRepository.save(new Skill(null, "SQL", it, true));
        skillRepository.save(new Skill(null, "React", it, true));
        skillRepository.save(new Skill(null, "Spring Boot", it, true));

        // Data Entry Role
        JobRole dataEntry = jobRoleRepository.save(new JobRole(null, "Data Entry Operator", "Office", true));
        skillRepository.save(new Skill(null, "MS Excel", dataEntry, true));
        skillRepository.save(new Skill(null, "Typing Speed 40+ WPM", dataEntry, true));
        skillRepository.save(new Skill(null, "English", dataEntry, true));

        // Sales Role
        JobRole sales = jobRoleRepository.save(new JobRole(null, "Sales Executive", "Sales", true));
        skillRepository.save(new Skill(null, "Communication", sales, true));
        skillRepository.save(new Skill(null, "Negotiation", sales, true));
        skillRepository.save(new Skill(null, "Field Sales", sales, true));

        // Support Role
        JobRole support = jobRoleRepository.save(new JobRole(null, "Customer Support", "Support", true));
        skillRepository.save(new Skill(null, "Phone Handling", support, true));
        skillRepository.save(new Skill(null, "Email Support", support, true));
        skillRepository.save(new Skill(null, "Problem Solving", support, true));

        // Other roles
        jobRoleRepository.save(new JobRole(null, "Delivery Partner", "Logistics", true));
        jobRoleRepository.save(new JobRole(null, "Accountant", "Finance", true));
        jobRoleRepository.save(new JobRole(null, "Teacher", "Education", true));
    }
}
