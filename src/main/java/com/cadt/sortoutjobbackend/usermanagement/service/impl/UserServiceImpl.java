package com.cadt.sortoutjobbackend.usermanagement.service.impl;

import com.cadt.sortoutjobbackend.common.exception.ResourceConflictException;
import com.cadt.sortoutjobbackend.usermanagement.dto.UserDTO;
import com.cadt.sortoutjobbackend.usermanagement.dto.UserRegistrationRequest;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.mapper.UserMapper;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import com.cadt.sortoutjobbackend.usermanagement.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
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
}
