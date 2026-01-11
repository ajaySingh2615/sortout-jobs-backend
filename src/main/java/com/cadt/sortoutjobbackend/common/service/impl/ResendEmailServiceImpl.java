package com.cadt.sortoutjobbackend.common.service.impl;

import com.cadt.sortoutjobbackend.common.service.EmailService;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailServiceImpl implements EmailService {

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.from-email}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            Resend resend = new Resend(apiKey);
            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .html(htmlContent)
                    .build();
            resend.emails().send(options);
        } catch (ResendException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}
