package com.cadt.sortoutjobbackend.profile.dto;

import com.cadt.sortoutjobbackend.profile.entity.GradeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationDTO {
    private Long id;
    private String degree;
    private String specialization;
    private String institution;
    private Integer passOutYear;
    private GradeType gradeType;
    private String grade;
}
