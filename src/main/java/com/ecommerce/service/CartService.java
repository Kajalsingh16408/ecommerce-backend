package com.ecommerce.service;

import com.ecommerce.dto.CartItemResponse;
import com.ecommerce.dto.CartResponse;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    cart.setItems(new ArrayList<>());
                    cart.setCreatedAt(LocalDateTime.now());
                    cart.setUpdatedAt(LocalDateTime.now());
                    return cartRepository.save(cart);
                });
    }

    // Add product to cart
    public CartResponse addItem(String email, String productId, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        User user = getUser(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.isActive()) {
            throw new RuntimeException("Product is not available");
        }

        Cart cart = getOrCreateCart(user.getId());

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        int newQuantity = quantity;

        if (existingItem != null) {
            newQuantity += existingItem.getQuantity();
        }

        if (newQuantity > product.getStock()) {
            throw new RuntimeException("Insufficient product stock");
        }

        if (existingItem != null) {
            existingItem.setQuantity(newQuantity);
        } else {
            cart.getItems().add(new CartItem(productId, quantity));
        }

        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);

        return buildCartResponse(cart);
    }

    // Update product quantity
    public CartResponse updateItem(
            String email, String productId, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        User user = getUser(email);
        Cart cart = getOrCreateCart(user.getId());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.isActive()) {
            throw new RuntimeException("Product is not available");
        }

        if (quantity > product.getStock()) {
            throw new RuntimeException("Insufficient product stock");
        }

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        item.setQuantity(quantity);
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);

        return buildCartResponse(cart);
    }

    // Remove product from cart
    public CartResponse removeItem(String email, String productId) {

        User user = getUser(email);
        Cart cart = getOrCreateCart(user.getId());

        boolean removed = cart.getItems().removeIf(
                item -> item.getProductId().equals(productId)
        );

        if (!removed) {
            throw new RuntimeException("Item not found in cart");
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return buildCartResponse(cart);
    }

    // Get user's cart
    public CartResponse getCart(String email) {

        User user = getUser(email);
        Cart cart = getOrCreateCart(user.getId());

        return buildCartResponse(cart);
    }

    // Clear cart
    public void clearCart(String email) {

        User user = getUser(email);
        cartRepository.deleteByUserId(user.getId());
    }

    // Build cart response with product details and total
    private CartResponse buildCartResponse(Cart cart) {

        List<CartItemResponse> responseItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            BigDecimal lineTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            responseItems.add(
                    new CartItemResponse(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            item.getQuantity(),
                            lineTotal
                    )
            );

            totalAmount = totalAmount.add(lineTotal);
        }

        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                responseItems,
                totalAmount
        );
    }
}