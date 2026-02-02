package com.cadt.sortoutjobbackend.usermanagement.service.impl;

import com.cadt.sortoutjobbackend.usermanagement.service.SmsService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsServiceImpl implements SmsService {

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @Override
    public void sendOtp(String phoneNumber, String otp) {
        Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioPhoneNumber),
                "Your sortout jobs verification code is: " + otp + ". valid for 5 minutes."
        ).create();
    }
}
