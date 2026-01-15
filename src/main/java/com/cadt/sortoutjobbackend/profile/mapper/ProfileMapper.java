package com.cadt.sortoutjobbackend.profile.mapper;

import com.cadt.sortoutjobbackend.profile.dto.*;
import com.cadt.sortoutjobbackend.profile.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    // Employment
    EmploymentDTO toEmploymentDTO(Employment employment);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Employment toEmployment(EmploymentDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEmployment(@MappingTarget Employment employment, EmploymentDTO dto);

    // Education
    EducationDTO toEducationDTO(Education education);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Education toEducation(EducationDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEducation(@MappingTarget Education education, EducationDTO dto);

    // Project
    ProjectDTO toProjectDTO(Project project);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Project toProject(ProjectDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateProject(@MappingTarget Project project, ProjectDTO dto);

    // IT Skill
    ITSkillDTO toITSkillDTO(ITSkill itSkill);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ITSkill toITSkill(ITSkillDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateITSkill(@MappingTarget ITSkill itSkill, ITSkillDTO dto);

    // Personal Details
    PersonalDetailsDTO toPersonalDetailsDTO(PersonalDetails personalDetails);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePersonalDetails(@MappingTarget PersonalDetails personalDetails, PersonalDetailsDTO dto);
}
