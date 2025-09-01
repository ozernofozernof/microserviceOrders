package com.example.orderservice.controller;

import com.example.orderservice.dto.*;
import com.example.orderservice.entity.User;
import com.example.orderservice.repository.UserRepository;
import com.example.orderservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest req) {
        return authService.login(req);
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal UserDetails principal) {
        User u = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        return new MeResponse(u.getId(), u.getUsername(), u.getRole());
    }
}
