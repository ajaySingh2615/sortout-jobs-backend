package com.cadt.sortoutjobbackend.profile.dto;

import com.cadt.sortoutjobbackend.profile.entity.EmploymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDTO {
    private Long id;
    private String designation;
    private String company;
    private EmploymentType employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private String noticePeriod;
    private String description;
}
