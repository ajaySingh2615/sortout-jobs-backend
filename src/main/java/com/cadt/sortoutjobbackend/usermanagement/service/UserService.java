package com.cadt.sortoutjobbackend.usermanagement.service;

import com.cadt.sortoutjobbackend.common.exception.ResourceConflictException;
import com.cadt.sortoutjobbackend.usermanagement.dto.UserDTO;
import com.cadt.sortoutjobbackend.usermanagement.dto.UserRegistrationRequest;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.mapper.UserMapper;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO createUser(UserRegistrationRequest request) {
        // check if user exits
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceConflictException("User", "email", request.getEmail());
        }

        // DTO -> Entity
        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // save entity
        User savedUser = userRepository.save(user);

        // Entity -> DTO
        return userMapper.toDTO(savedUser);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toDTO);
    }


}
