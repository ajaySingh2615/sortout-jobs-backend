package com.cadt.sortoutjobbackend.common.service.impl;

import com.cadt.sortoutjobbackend.common.service.EmailService;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ResendEmailServiceImpl.class);

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.from-email}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            log.info("Attempting to send email to: {}", to);

            if (apiKey == null || apiKey.isEmpty()) {
                log.error("Resend API key is not configured");
                throw new RuntimeException(
                        "Email service not configured. Please check RESEND_API_KEY environment variable.");
            }

            if (fromEmail == null || fromEmail.isEmpty()) {
                log.error("Resend from email is not configured");
                throw new RuntimeException(
                        "Email service not configured. Please check RESEND_FROM_EMAIL environment variable.");
            }

            Resend resend = new Resend(apiKey);
            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .html(htmlContent)
                    .build();

            var response = resend.emails().send(options);
            log.info("Email sent successfully to: {} - Message ID: {}", to, response.getId());
        } catch (ResendException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
