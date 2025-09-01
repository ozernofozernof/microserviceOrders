package com.example.orderservice.dto;

import com.example.orderservice.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderResponse {
    private UUID id;
    private String description;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public OrderResponse(UUID id, String description, OrderStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
