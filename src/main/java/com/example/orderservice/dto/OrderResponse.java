package com.example.orderservice.dto;

import com.example.orderservice.entity.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class OrderResponse {
    private final UUID id;
    private final String description;
    private final OrderStatus status;
    private final LocalDateTime createdAt;

    public OrderResponse(UUID id, String description, OrderStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

}
