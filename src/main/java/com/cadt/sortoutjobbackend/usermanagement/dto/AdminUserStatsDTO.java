package com.cadt.sortoutjobbackend.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for admin dashboard user statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserStatsDTO {
    private long totalUsers;
    private long activeUsers;
    private long disabledUsers;
    private long jobSeekers;
    private long recruiters;
    private long admins;
    private long newUsersThisWeek;
    private long newUsersThisMonth;
}
