package com.cadt.sortoutjobbackend.profile.dto;

import com.cadt.sortoutjobbackend.profile.entity.Proficiency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ITSkillDTO {
    private Long id;
    private String name;
    private Proficiency proficiency;
    private Integer experienceMonths;
}
