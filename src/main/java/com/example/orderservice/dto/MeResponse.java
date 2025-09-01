package com.example.orderservice.dto;

import com.example.orderservice.entity.Role;
import java.util.UUID;

public class MeResponse {
    private UUID id;
    private String username;
    private Role role;

    public MeResponse(UUID id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }
}
