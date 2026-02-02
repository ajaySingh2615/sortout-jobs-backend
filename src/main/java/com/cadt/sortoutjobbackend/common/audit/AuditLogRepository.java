package com.cadt.sortoutjobbackend.common.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    // Find logs by user
    List<AuditLog> findByUserIdOrderByTimestampDesc(String userId);
    List<AuditLog> findByUserEmailOrderByTimestampDesc(String email);
    
    // Find by action type
    List<AuditLog> findByActionOrderByTimestampDesc(String action);
    
    // Find failures
    List<AuditLog> findByStatusOrderByTimestampDesc(String status);
    
    // Find by endpoint
    List<AuditLog> findByEndpointOrderByTimestampDesc(String endpoint);
    
    // Find by IP (detect suspicious activity)
    List<AuditLog> findByIpAddressOrderByTimestampDesc(String ipAddress);
    
    // Find logs in time range
    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(Instant start, Instant end);
}
