package com.example.orderservice.controller;

import com.example.orderservice.entity.User;
import com.example.orderservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) { this.userRepository = userRepository; }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> all() { return userRepository.findAll(); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) { userRepository.deleteById(id); }
}
