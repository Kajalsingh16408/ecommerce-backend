
package com.ecommerce.service;

import com.ecommerce.dto.PlaceOrderRequest;
import com.ecommerce.model.*;
import com.ecommerce.repository.*;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Order placeOrder(String email, PlaceOrderRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request == null ||
                request.getShippingAddress() == null ||
                request.getShippingAddress().isBlank()) {
            throw new IllegalArgumentException("Shipping address is required");
        }

        if (request.getPaymentMethod() == null ||
                request.getPaymentMethod().isBlank()) {
            throw new IllegalArgumentException("Payment method is required");
        }

        String paymentMethod = request.getPaymentMethod()
                .trim().toUpperCase(Locale.ROOT);

        if (!List.of("COD", "UPI", "CARD").contains(paymentMethod)) {
            throw new IllegalArgumentException("Invalid payment method");
        }

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        List<Product> productsToUpdate = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Validate all products before changing stock
        for (CartItem cartItem : cart.getItems()) {

            Product product = productRepository.findById(
                    cartItem.getProductId()
            ).orElseThrow(() -> new RuntimeException("Product not found"));

            if (!product.isActive()) {
                throw new RuntimeException(
                        product.getName() + " is unavailable");
            }

            if (cartItem.getQuantity() <= 0 ||
                    cartItem.getQuantity() > product.getStock()) {
                throw new RuntimeException(
                        "Insufficient stock for " + product.getName());
            }

            if (product.getPrice() == null ||
                    product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Invalid product price");
            }

            BigDecimal lineTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            orderItems.add(new OrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    cartItem.getQuantity(),
                    lineTotal
            ));

            totalAmount = totalAmount.add(lineTotal);

            productsToUpdate.add(product);
        }

        Order order = new Order();
        order.setUserId(user.getId());
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setShippingAddress(request.getShippingAddress().trim());
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Save order first
        Order savedOrder = orderRepository.save(order);

        // Deduct stock after successful order save
        for (int i = 0; i < productsToUpdate.size(); i++) {

            Product product = productsToUpdate.get(i);
            int quantity = cart.getItems().get(i).getQuantity();

            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
        }

        // Clear cart after successful processing
        cartRepository.deleteByUserId(user.getId());

        return savedOrder;
    }

    public List<Order> getMyOrders(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUserIdOrderByCreatedAtDesc(
                user.getId());
    }

    public Order getOrderById(String email, String orderId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order cancelOrder(String email, String orderId) {

        Order order = getOrderById(email, orderId);

        if (order.getStatus() != OrderStatus.PENDING &&
                order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("This order cannot be cancelled");
        }

        // Restore stock
        for (OrderItem item : order.getItems()) {

            Product product = productRepository.findById(
                    item.getProductId()
            ).orElseThrow(() -> new RuntimeException("Product not found"));

            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }
}
