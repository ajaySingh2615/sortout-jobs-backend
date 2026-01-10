package com.cadt.sortoutjobbackend.usermanagement.service;

public interface SmsService {
    void sendOtp(String phoneNumber, String otp);
}
