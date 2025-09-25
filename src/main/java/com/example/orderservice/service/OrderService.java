package com.example.orderservice.service;

import com.example.orderservice.dto.OrderCreateRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.OrderUpdateStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse create(OrderCreateRequest request, Authentication authentication);

    List<OrderResponse> listMine(Authentication authentication);

    Page<OrderResponse> listAll(int page, int size);

    OrderResponse updateStatus(UUID id, OrderUpdateStatusRequest request);

    void delete(UUID id, Authentication authentication);
}


