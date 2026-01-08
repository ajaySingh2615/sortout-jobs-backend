package com.cadt.sortoutjobbackend.usermanagement.service;

import com.cadt.sortoutjobbackend.usermanagement.dto.UserDTO;
import com.cadt.sortoutjobbackend.usermanagement.dto.UserRegistrationRequest;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import com.cadt.sortoutjobbackend.usermanagement.mapper.UserMapper;
import com.cadt.sortoutjobbackend.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO createUser(UserRegistrationRequest request) {
        // 1. DTO -> Entity
        User user = userMapper.toEntity(request);

        // 2. save entity
        User savedUser = userRepository.save(user);

        // 3. Entity -> DTO
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

    // --- MAPPERS (We will replace these with MapStruct later) ---

//    // mapper
//    private User mapToEntity(UserRegistrationRequest request) {
//        User user = new User();
//        user.setEmail(request.getEmail());
//        user.setPassword(request.getPassword());  // TODO: Encrypt this later!
//        user.setRole(request.getRole());
//        return user;
//    }
//
//    // Entity -> Response DTO
//    private UserDTO mapToDTO(User user) {
//        UserDTO dto = new UserDTO();
//        dto.setId(user.getId());
//        dto.setEmail(user.getEmail());
//        dto.setRole(user.getRole());
//        return dto;
//    }
}
