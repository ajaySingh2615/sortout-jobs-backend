package com.cadt.sortoutjobbackend.common.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Log a successful action
     */
    public void logSuccess(HttpServletRequest request, String action, String userEmail) {
        log(request, action, "SUCCESS", 200, userEmail, null, null);
    }

    /**
     * Log a failed action
     */
    public void logFailure(HttpServletRequest request, String action, String userEmail, 
                           int statusCode, String errorCode, String errorMessage) {
        log(request, action, "FAILED", statusCode, userEmail, errorCode, errorMessage);
    }

    /**
     * Log a rate-limited request
     */
    public void logRateLimited(HttpServletRequest request, String action, String identifier) {
        log(request, action, "RATE_LIMITED", 429, identifier, "RATE_001", "Too many requests");
    }

    /**
     * Core logging method
     */
    private void log(HttpServletRequest request, String action, String status, 
                     int statusCode, String userEmail, String errorCode, String errorMessage) {
        try {
            String userId = null;
            
            // Try to get authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                userId = auth.getName();
            }

            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .userEmail(userEmail)
                    .ipAddress(getClientIp(request))
                    .method(request.getMethod())
                    .endpoint(request.getRequestURI())
                    .userAgent(request.getHeader("User-Agent"))
                    .action(action)
                    .status(status)
                    .statusCode(statusCode)
                    .errorCode(errorCode)
                    .errorMessage(errorMessage != null ? errorMessage.substring(0, Math.min(errorMessage.length(), 1000)) : null)
                    .timestamp(Instant.now())
                    .build();

            auditLogRepository.save(auditLog);
            
            // Also log to console
            if ("SUCCESS".equals(status)) {
                log.info("[AUDIT] {} - {} - {} - {}", action, status, userEmail, request.getRequestURI());
            } else {
                log.warn("[AUDIT] {} - {} - {} - {} - {}", action, status, userEmail, errorCode, request.getRequestURI());
            }
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    /**
     * Get client IP (handles proxies)
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        // Take first IP if multiple (X-Forwarded-For can have multiple)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
