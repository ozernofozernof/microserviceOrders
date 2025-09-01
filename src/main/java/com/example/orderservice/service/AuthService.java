package com.example.orderservice.service;

import com.example.orderservice.config.JwtService;
import com.example.orderservice.dto.AuthRequest;
import com.example.orderservice.dto.AuthResponse;
import com.example.orderservice.dto.RegisterRequest;
import com.example.orderservice.entity.Role;
import com.example.orderservice.entity.User;
import com.example.orderservice.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder encoder, AuthenticationManager authManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setRole(Role.USER);
        userRepository.save(u);
    }

    public AuthResponse login(AuthRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        var user = userRepository.findByUsername(req.getUsername()).orElseThrow();
        String token = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(),
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        ));
        return new AuthResponse(token);
    }
}
