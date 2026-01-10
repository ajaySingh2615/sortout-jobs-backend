package com.cadt.sortoutjobbackend.usermanagement.service.impl;

import com.cadt.sortoutjobbackend.common.exception.ResourceConflictException;
import com.cadt.sortoutjobbackend.usermanagement.dto.*;
import com.cadt.sortoutjobbackend.usermanagement.entity.Otp;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.mapper.UserMapper;
import com.cadt.sortoutjobbackend.usermanagement.repository.OtpRepository;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import com.cadt.sortoutjobbackend.usermanagement.service.SmsService;
import com.cadt.sortoutjobbackend.usermanagement.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;
    private final SmsService smsService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           PasswordEncoder passwordEncoder, OtpRepository otpRepository, SmsService smsService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.otpRepository = otpRepository;
        this.smsService = smsService;
    }

    @Override
    public UserDTO createUser(UserRegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceConflictException("User", "email", request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toDTO);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDTO updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getProfilePicture() != null) {
            user.setProfilePicture(request.getProfilePicture());
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // check if user has a password (not OAuth/Phone)
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("Cannot change password for OAuth/Phone accounts");
        }

        // verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Set new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void linkPhone(Long userId, LinkPhoneRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify OTP
        Otp otp = otpRepository.findByPhoneAndVerifiedFalse(request.getPhone())
                .orElseThrow(() -> new RuntimeException("OTP expired"));

        if (otp.getExpiryTime().isBefore(Instant.now())) {
            otpRepository.delete(otp);
            throw new RuntimeException("OTP expired");
        }

        if (!otp.getOtpCode().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        // Link phone
        user.setPhone(request.getPhone());
        userRepository.save(user);

        otpRepository.delete(otp);
    }

    @Override
    @Transactional
    public void sendPhoneLinkOtp(Long userId, String phone) {
        // Check if phone already used
        if (userRepository.findByPhone(phone).isPresent()) {
            throw new RuntimeException("Phone number already linked to another account");
        }

        // Delete existing OTP
        otpRepository.deleteByPhone(phone);

        // Generate OTP
        String otpCode = String.format("%06d", new Random().nextInt(999999));

        Otp otp = new Otp();
        otp.setPhone(phone);
        otp.setOtpCode(otpCode);
        otp.setExpiryTime(Instant.now().plusSeconds(300));  // 5 mins
        otpRepository.save(otp);

        smsService.sendOtp(phone, otpCode);
    }
}
