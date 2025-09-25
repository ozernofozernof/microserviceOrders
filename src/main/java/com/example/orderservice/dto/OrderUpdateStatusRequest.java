package com.example.orderservice.dto;

import com.example.orderservice.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrderUpdateStatusRequest {
    @NotNull
    private OrderStatus status;

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
