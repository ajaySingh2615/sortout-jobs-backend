package com.cadt.sortoutjobbackend.common.audit;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Instant;

@Component
@Order(1) // Run first
public class AuditFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuditFilter.class);
    
    private final AuditLogRepository auditLogRepository;

    public AuditFilter(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Skip static resources and actuator
        String path = httpRequest.getRequestURI();
        if (shouldSkip(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Wrap for caching
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();
        
        try {
            // Process request
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            // Log after request completes
            long duration = System.currentTimeMillis() - startTime;
            logRequest(wrappedRequest, wrappedResponse, duration);
            
            // Copy response body back
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(HttpServletRequest request, HttpServletResponse response, long duration) {
        try {
            String userId = null;
            String userEmail = null;
            
            // Get authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                userEmail = auth.getName();
                userId = auth.getName(); // In our case, username is email
            }

            int statusCode = response.getStatus();
            String status = statusCode >= 200 && statusCode < 400 ? "SUCCESS" : "FAILED";
            String action = determineAction(request.getMethod(), request.getRequestURI());

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
                    .timestamp(Instant.now())
                    .responseTimeMs(duration)
                    .build();

            auditLogRepository.save(auditLog);

            // Console log
            log.info("[AUDIT] {} {} {} - {} - {}ms - IP: {}", 
                    request.getMethod(), 
                    request.getRequestURI(), 
                    statusCode,
                    userEmail != null ? userEmail : "anonymous",
                    duration,
                    getClientIp(request));
                    
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    private String determineAction(String method, String path) {
        // Determine action based on endpoint
        if (path.contains("/login")) return "LOGIN";
        if (path.contains("/register")) return "REGISTER";
        if (path.contains("/logout")) return "LOGOUT";
        if (path.contains("/refresh-token")) return "REFRESH_TOKEN";
        if (path.contains("/forgot-password")) return "FORGOT_PASSWORD";
        if (path.contains("/reset-password")) return "RESET_PASSWORD";
        if (path.contains("/verify-email")) return "VERIFY_EMAIL";
        if (path.contains("/send-otp")) return "SEND_OTP";
        if (path.contains("/verify-otp")) return "VERIFY_OTP";
        if (path.contains("/profile")) return "PROFILE_" + method;
        if (path.contains("/users")) return "USER_" + method;
        if (path.contains("/sessions")) return "SESSION_" + method;
        return method + "_" + path;
    }

    private boolean shouldSkip(String path) {
        return path.startsWith("/css") ||
               path.startsWith("/js") ||
               path.startsWith("/images") ||
               path.startsWith("/favicon") ||
               path.startsWith("/actuator") ||
               path.equals("/error");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
