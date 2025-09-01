package com.example.orderservice.dto;

import jakarta.validation.constraints.NotBlank;

public class OrderCreateRequest {
    @NotBlank
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
