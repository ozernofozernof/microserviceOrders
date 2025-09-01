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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    private User currentUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName()).orElseThrow();
    }

    public OrderResponse create(OrderCreateRequest req, Authentication auth) {
        User u = currentUser(auth);
        Order o = new Order();
        o.setUser(u);
        o.setDescription(req.getDescription());
        o.setStatus(OrderStatus.CREATED);
        orderRepository.save(o);
        return toDto(o);
    }

    public List<OrderResponse> listMine(Authentication auth) {
        User u = currentUser(auth);
        return orderRepository.findByUser_IdOrderByCreatedAtDesc(u.getId())
                .stream().map(this::toDto).toList();
    }

    public Page<OrderResponse> listAll(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size)).map(this::toDto);
    }

    public OrderResponse updateStatus(UUID id, OrderUpdateStatusRequest req) {
        Order o = orderRepository.findById(id).orElseThrow();
        o.setStatus(req.getStatus());
        return toDto(orderRepository.save(o));
    }

    public void delete(UUID id, Authentication auth) {
        Order o = orderRepository.findById(id).orElseThrow();
        User me = currentUser(auth);
        boolean isOwner = o.getUser().getUsername().equals(me.getUsername());
        boolean isAdmin = me.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new SecurityException("Forbidden: only owner or admin can delete");
        }
        orderRepository.delete(o);
    }

    private OrderResponse toDto(Order o) {
        return new OrderResponse(o.getId(), o.getDescription(), o.getStatus(), o.getCreatedAt());
    }
}
