package com.example.orderservice.dto;

import com.example.orderservice.entity.Role;
import lombok.Getter;

import java.util.UUID;

@Getter
public class MeResponse {
    private final UUID id;
    private final String username;
    private final Role role;

    public MeResponse(UUID id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

}
