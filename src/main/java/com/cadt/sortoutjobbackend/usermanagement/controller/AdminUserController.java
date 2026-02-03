package com.cadt.sortoutjobbackend.usermanagement.controller;

import com.cadt.sortoutjobbackend.common.dto.ApiResponse;
import com.cadt.sortoutjobbackend.usermanagement.dto.*;
import com.cadt.sortoutjobbackend.usermanagement.entity.AuthProvider;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import com.cadt.sortoutjobbackend.usermanagement.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final UserRepository userRepository;

    // ==================== LIST & STATS ====================

    /**
     * Get paginated list of users with search and filters
     */
    @GetMapping
    public ResponseEntity<ApiResponse<AdminUserListResponse>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) AuthProvider authProvider,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        AdminUserListResponse response = adminUserService.getAllUsers(search, role, isActive, authProvider, page, size);
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", response));
    }

    /**
     * Get user statistics for dashboard
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminUserStatsDTO>> getUserStats() {
        AdminUserStatsDTO stats = adminUserService.getUserStats();
        return ResponseEntity.ok(ApiResponse.success("User stats fetched successfully", stats));
    }

    // ==================== USER CRUD ====================

    /**
     * Get detailed user information
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserDetailDTO>> getUserDetail(@PathVariable Long userId) {
        AdminUserDetailDTO user = adminUserService.getUserDetail(userId);
        return ResponseEntity.ok(ApiResponse.success("User details fetched successfully", user));
    }

    /**
     * Update user details
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserDTO>> updateUser(
            @PathVariable Long userId,
            @RequestBody AdminUpdateUserRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long adminId = getAdminId(userDetails);
        AdminUserDTO user = adminUserService.updateUser(userId, request, adminId);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", user));
    }

    /**
     * Toggle user active status
     */
    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean isActive
    ) {
        adminUserService.toggleUserStatus(userId, isActive);
        String message = isActive ? "User activated successfully" : "User disabled successfully";
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     * Manually verify user's email
     */
    @PatchMapping("/{userId}/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyUserEmail(@PathVariable Long userId) {
        adminUserService.verifyUserEmail(userId);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully"));
    }

    /**
     * Send password reset email to user
     */
    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<ApiResponse<Void>> sendPasswordReset(@PathVariable Long userId) {
        adminUserService.sendPasswordReset(userId);
        return ResponseEntity.ok(ApiResponse.success("Password reset email sent successfully"));
    }

    /**
     * Delete user account
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long adminId = getAdminId(userDetails);
        adminUserService.deleteUser(userId, adminId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    // ==================== SESSION MANAGEMENT ====================

    /**
     * Get user's active sessions
     */
    @GetMapping("/{userId}/sessions")
    public ResponseEntity<ApiResponse<List<SessionDTO>>> getUserSessions(@PathVariable Long userId) {
        List<SessionDTO> sessions = adminUserService.getUserSessions(userId);
        return ResponseEntity.ok(ApiResponse.success("Sessions fetched successfully", sessions));
    }

    /**
     * Revoke all user sessions
     */
    @DeleteMapping("/{userId}/sessions")
    public ResponseEntity<ApiResponse<Void>> revokeAllSessions(@PathVariable Long userId) {
        adminUserService.revokeAllSessions(userId);
        return ResponseEntity.ok(ApiResponse.success("All sessions revoked successfully"));
    }

    /**
     * Revoke specific session
     */
    @DeleteMapping("/{userId}/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> revokeSession(
            @PathVariable Long userId,
            @PathVariable Long sessionId
    ) {
        adminUserService.revokeSession(userId, sessionId);
        return ResponseEntity.ok(ApiResponse.success("Session revoked successfully"));
    }

    // ==================== HELPER METHODS ====================

    private Long getAdminId(UserDetails userDetails) {
        User admin = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return admin.getId();
    }
}
