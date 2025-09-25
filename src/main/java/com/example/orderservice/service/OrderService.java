package com.example.orderservice.service;

import com.example.orderservice.dto.OrderCreateRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.OrderUpdateStatusRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.entity.Role;
import com.example.orderservice.entity.User;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private User currentUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName()).orElseThrow();
    }

    public OrderResponse create(OrderCreateRequest req, Authentication auth) {
        User user = currentUser(auth);
        Order order = new Order();
        order.setUser(user);
        order.setDescription(req.getDescription());
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);
        return toDto(order);
    }

    public List<OrderResponse> listMine(Authentication auth) {
        User user = currentUser(auth);
        return orderRepository.findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toDto).toList();
    }

    public Page<OrderResponse> listAll(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size)).map(this::toDto);
    }

    public OrderResponse updateStatus(UUID id, OrderUpdateStatusRequest req) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(req.getStatus());
        return toDto(orderRepository.save(order));
    }

    public void delete(UUID id, Authentication auth) {
        Order order = orderRepository.findById(id).orElseThrow();
        User me = currentUser(auth);
        boolean isOwner = order.getUser().getUsername().equals(me.getUsername());
        boolean isAdmin = me.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new SecurityException("Forbidden: only owner or admin can delete");
        }
        orderRepository.delete(order);
    }

    private OrderResponse toDto(Order order) {
        return new OrderResponse(order.getId(), order.getDescription(), order.getStatus(), order.getCreatedAt());
    }
}
