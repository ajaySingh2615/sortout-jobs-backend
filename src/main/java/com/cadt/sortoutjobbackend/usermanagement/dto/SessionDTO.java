package com.cadt.sortoutjobbackend.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDTO {
    private Long id;
    private String tokenPreview;  // Only show first 8 chars for security
    private Instant createdAt;
    private Instant expiryDate;
    private boolean isCurrentSession;

}
