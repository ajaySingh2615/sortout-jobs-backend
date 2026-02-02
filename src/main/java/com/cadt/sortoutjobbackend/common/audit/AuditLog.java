package com.cadt.sortoutjobbackend.common.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who made the request
    private String userId;        // User ID if authenticated
    private String userEmail;     // User email if available
    
    // Request details
    private String ipAddress;
    private String method;        // GET, POST, PUT, DELETE
    private String endpoint;      // /api/auth/login
    private String userAgent;     // Browser/Client info
    
    // What happened
    private String action;        // LOGIN, REGISTER, PASSWORD_RESET, etc.
    private String status;        // SUCCESS, FAILED, RATE_LIMITED
    private Integer statusCode;   // 200, 401, 429, 500
    
    // Error details (if any)
    @Column(length = 1000)
    private String errorMessage;
    private String errorCode;     // AUTH_001, RATE_001, etc.
    
    // Timing
    private Instant timestamp;
    private Long responseTimeMs;  // How long the request took
}
