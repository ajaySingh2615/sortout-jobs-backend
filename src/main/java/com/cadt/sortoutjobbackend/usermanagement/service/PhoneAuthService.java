package com.cadt.sortoutjobbackend.usermanagement.service;

import com.cadt.sortoutjobbackend.usermanagement.dto.LoginResponse;

public interface PhoneAuthService {

    void sendOtp(String phone);

    LoginResponse verifyOtpAndLogin(String phone, String otp);
}
