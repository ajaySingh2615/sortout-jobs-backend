package com.cadt.sortoutjobbackend.usermanagement.controller;

import com.cadt.sortoutjobbackend.usermanagement.dto.UserDTO;
import com.cadt.sortoutjobbackend.usermanagement.dto.UserRegistrationRequest;
import com.cadt.sortoutjobbackend.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserRegistrationRequest request) {
        UserDTO savedUser = userService.createUser(request);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
