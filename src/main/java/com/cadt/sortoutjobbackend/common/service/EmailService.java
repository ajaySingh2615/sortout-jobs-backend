package com.cadt.sortoutjobbackend.common.service;

/**
 * Common Email Service - Used across all modules
 */
public interface EmailService {
    void sendEmail(String to, String subject, String htmlContent);
}
