package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderCreateRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.OrderUpdateStatusRequest;
import com.example.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody OrderCreateRequest req, Authentication auth) {
        return orderService.create(req, auth);
    }

    @GetMapping
    public List<OrderResponse> my(Authentication auth) { return orderService.listMine(auth); }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<OrderResponse> all(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        return orderService.listAll(page, size);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse update(@PathVariable UUID id, @Valid @RequestBody OrderUpdateStatusRequest req) {
        return orderService.updateStatus(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, Authentication auth) { orderService.delete(id, auth); }
}
