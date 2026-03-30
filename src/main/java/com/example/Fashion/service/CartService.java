package com.example.Fashion.service;

import com.example.Fashion.dto.CartItemDTO;
import com.example.Fashion.model.*;
import com.example.Fashion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<CartItemDTO> getCartItems(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cart.getCartItems().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public List<CartItemDTO> addToCart(Long userId, Long productId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if item already exists in cart
        Optional<CartItem> existing = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existing.isPresent()) {
            // Increase quantity if already in cart
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            // Add new cart item
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cart.getCartItems().add(newItem);
        }

        cartRepository.save(cart);
        return getCartItems(userId);
    }

    @Transactional
    public List<CartItemDTO> removeFromCart(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
        cartRepository.save(cart);
        return getCartItems(userId);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = Cart.builder()
                    .user(user)
                    .cartItems(new ArrayList<>())
                    .build();
            return cartRepository.save(newCart);
        });
    }

    private CartItemDTO toDTO(CartItem item) {
        return CartItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImage(item.getProduct().getImageUrl())
                .productPrice(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                .build();
    }
}