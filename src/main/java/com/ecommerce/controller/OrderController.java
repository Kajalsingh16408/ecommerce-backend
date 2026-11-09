package com.ecommerce.controller;

import com.ecommerce.dto.PlaceOrderRequest;
import com.ecommerce.model.Order;
import com.ecommerce.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request,
            Principal principal) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(
                        principal.getName(), request));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders(
            Principal principal) {

        return ResponseEntity.ok(
                orderService.getMyOrders(principal.getName()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(
            @PathVariable String orderId,
            Principal principal) {

        return ResponseEntity.ok(
                orderService.getOrderById(
                        principal.getName(), orderId));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable String orderId,
            Principal principal) {

        return ResponseEntity.ok(
                orderService.cancelOrder(
                        principal.getName(), orderId));
    }
}