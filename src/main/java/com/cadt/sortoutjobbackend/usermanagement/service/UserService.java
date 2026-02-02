package com.cadt.sortoutjobbackend.usermanagement.service;

import com.cadt.sortoutjobbackend.usermanagement.dto.*;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO createUser(UserRegistrationRequest request);

    List<UserDTO> getAllUsers();

    Optional<UserDTO> getUserById(Long id);

    void deleteUser(Long id);

    UserDTO updateProfile(Long userId, UpdateProfileRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);

    void linkPhone(Long userId, LinkPhoneRequest request);

    void sendPhoneLinkOtp(Long userId, String phone);
}


