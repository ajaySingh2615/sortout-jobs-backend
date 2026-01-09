package com.cadt.sortoutjobbackend.usermanagement.service;

import com.cadt.sortoutjobbackend.usermanagement.dto.UserDTO;
import com.cadt.sortoutjobbackend.usermanagement.dto.UserRegistrationRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO createUser(UserRegistrationRequest request);
    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(Long id);
}
