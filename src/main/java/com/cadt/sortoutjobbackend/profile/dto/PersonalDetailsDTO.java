package com.cadt.sortoutjobbackend.profile.dto;

import com.cadt.sortoutjobbackend.profile.entity.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDetailsDTO {
    private LocalDate dateOfBirth;
    private MaritalStatus maritalStatus;
    private String address;
    private String pincode;
    private String nationality;
}
