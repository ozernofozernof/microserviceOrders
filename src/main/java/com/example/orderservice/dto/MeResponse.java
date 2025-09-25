package com.example.orderservice.dto;

import com.example.orderservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MeResponse {
    private UUID id;
    private String username;
    private Role role;
}

