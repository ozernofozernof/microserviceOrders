package com.example.orderservice.service;

import com.example.orderservice.dto.OrderCreateRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.OrderUpdateStatusRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.entity.Role;
import com.example.orderservice.entity.User;
import com.example.orderservice.error.ResourceNotFoundException;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private User currentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public OrderResponse create(OrderCreateRequest request, Authentication authentication) {
        User user = currentUser(authentication);
        Order order = new Order();
        order.setUser(user);
        order.setDescription(request.getDescription());
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);
        return toDto(order);
    }

    @Override
    public List<OrderResponse> listMine(Authentication authentication) {
        User user = currentUser(authentication);
        return orderRepository.findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toDto).toList();
    }

    @Override
    public Page<OrderResponse> listAll(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size)).map(this::toDto);
    }

    @Override
    public OrderResponse updateStatus(UUID id, OrderUpdateStatusRequest request) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(request.getStatus());
        return toDto(orderRepository.save(order));
    }

    @Override
    public void delete(UUID id, Authentication authentication) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        User currentUser = currentUser(authentication);
        boolean isOwner = order.getUser().getUsername().equals(currentUser.getUsername());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new SecurityException("Forbidden: only owner or admin can delete");
        }
        orderRepository.delete(order);
    }

    private OrderResponse toDto(Order order) {
        return new OrderResponse(order.getId(), order.getDescription(), order.getStatus(), order.getCreatedAt());
    }
}

