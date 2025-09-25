package com.example.orderservice.dto;

import com.example.orderservice.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private String description;
    private OrderStatus status;
    private LocalDateTime createdAt;
}

