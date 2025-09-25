package com.example.orderservice.mapper;

import com.example.orderservice.dto.OrderCreateRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OrderMapper {

    public Order toEntity(OrderCreateRequest request) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setDescription(request.getDescription());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getDescription(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
