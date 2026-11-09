package com.ecommerce.controller;

import com.ecommerce.dto.CartResponse;
import com.ecommerce.service.CartService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Get cart
    @GetMapping
    public ResponseEntity<CartResponse> getCart(Principal principal) {
        return ResponseEntity.ok(
                cartService.getCart(principal.getName())
        );
    }

    // Add item
    @PostMapping("/items/{productId}")
    public ResponseEntity<CartResponse> addItem(
            @PathVariable String productId,
            @RequestParam(defaultValue = "1") int quantity,
            Principal principal) {

        return ResponseEntity.ok(
                cartService.addItem(
                        principal.getName(), productId, quantity
                )
        );
    }

    // Update quantity
    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable String productId,
            @RequestParam int quantity,
            Principal principal) {

        return ResponseEntity.ok(
                cartService.updateItem(
                        principal.getName(), productId, quantity
                )
        );
    }

    // Remove item
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable String productId,
            Principal principal) {

        return ResponseEntity.ok(
                cartService.removeItem(
                        principal.getName(), productId
                )
        );
    }

    // Clear cart
    @DeleteMapping
    public ResponseEntity<String> clearCart(Principal principal) {

        cartService.clearCart(principal.getName());

        return ResponseEntity.ok("Cart cleared successfully");
    }
}