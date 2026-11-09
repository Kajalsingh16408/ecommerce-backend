
package com.ecommerce.controller;

import com.ecommerce.dto.OrderStatusRequest;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.Product;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public AdminController(
            ProductRepository productRepository,
            OrderRepository orderRepository) {

        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(
            @Valid @RequestBody Product product) {

        product.setId(null);
        Product saved = productRepository.save(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody Product updatedProduct) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setCategory(updatedProduct.getCategory());
        product.setPrice(updatedProduct.getPrice());
        product.setStock(updatedProduct.getStock());
        product.setActive(updatedProduct.isActive());

        return ResponseEntity.ok(productRepository.save(product));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {

        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }

        productRepository.deleteById(id);

        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody OrderStatusRequest request) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus current = order.getStatus();
        OrderStatus next = request.getStatus();

        if (next == null) {
            throw new IllegalArgumentException("Order status is required");
        }

        boolean validTransition =
                (current == OrderStatus.PENDING &&
                        next == OrderStatus.CONFIRMED)
                ||
                (current == OrderStatus.CONFIRMED &&
                        next == OrderStatus.SHIPPED)
                ||
                (current == OrderStatus.SHIPPED &&
                        next == OrderStatus.DELIVERED);

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Invalid order status transition: "
                            + current + " to " + next);
        }

        order.setStatus(next);
        order.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(orderRepository.save(order));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {

        return ResponseEntity.ok(Map.of(
                "totalProducts", productRepository.count(),
                "totalOrders", orderRepository.count(),
                "message", "Admin dashboard data"
        ));
    }
}
