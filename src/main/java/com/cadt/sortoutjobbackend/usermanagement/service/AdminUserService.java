package com.cadt.sortoutjobbackend.usermanagement.service;

import com.cadt.sortoutjobbackend.usermanagement.dto.*;
import com.cadt.sortoutjobbackend.usermanagement.entity.AuthProvider;

import java.util.List;

/**
 * Service for admin user management operations
 */
public interface AdminUserService {

    /**
     * Get paginated list of users with search and filters
     */
    AdminUserListResponse getAllUsers(
            String search,
            String role,
            Boolean isActive,
            AuthProvider authProvider,
            int page,
            int size
    );

    /**
     * Get user statistics for admin dashboard
     */
    AdminUserStatsDTO getUserStats();

    /**
     * Get detailed user information including related data
     */
    AdminUserDetailDTO getUserDetail(Long userId);

    /**
     * Update user details (admin can change any field)
     */
    AdminUserDTO updateUser(Long userId, AdminUpdateUserRequest request, Long adminId);

    /**
     * Toggle user active status
     */
    void toggleUserStatus(Long userId, boolean isActive);

    /**
     * Manually verify user's email
     */
    void verifyUserEmail(Long userId);

    /**
     * Send password reset email to user
     */
    void sendPasswordReset(Long userId);

    /**
     * Get user's active sessions
     */
    List<SessionDTO> getUserSessions(Long userId);

    /**
     * Revoke all user sessions
     */
    void revokeAllSessions(Long userId);

    /**
     * Revoke a specific session
     */
    void revokeSession(Long userId, Long sessionId);

    /**
     * Delete user account and related data
     */
    void deleteUser(Long userId, Long adminId);
}
