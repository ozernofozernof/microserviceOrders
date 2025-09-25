package com.example.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OrderCreateRequest {
    @NotBlank
    private String description;

    public void setDescription(String description) {
        this.description = description;
    }
}
