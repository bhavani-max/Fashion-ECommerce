package com.example.Fashion.service;

import com.example.Fashion.dto.WishlistDTO;
import com.example.Fashion.model.*;
import com.example.Fashion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages wishlist operations: add, remove, and fetch products saved by a user.
 */
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /** Returns all wishlisted products for a user */
    public List<WishlistDTO> getWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /** Adds a product to the wishlist; ignores duplicates */
    @Transactional
    public WishlistDTO addToWishlist(Long userId, Long productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            return toDTO(wishlistRepository.findByUserIdAndProductId(userId, productId).get());
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Wishlist saved = wishlistRepository.save(
                Wishlist.builder().user(user).product(product).build()
        );
        return toDTO(saved);
    }

    /** Removes a product from the wishlist */
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    /** Checks if a product is in a user's wishlist */
    public boolean isWishlisted(Long userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    private WishlistDTO toDTO(Wishlist w) {
        return WishlistDTO.builder()
                .id(w.getId())
                .productId(w.getProduct().getId())
                .productName(w.getProduct().getName())
                .productImage(w.getProduct().getImageUrl())
                .productPrice(w.getProduct().getPrice())
                .brand(w.getProduct().getBrand())
                .categoryName(w.getProduct().getCategory() != null
                        ? w.getProduct().getCategory().getName() : null)
                .build();
    }
}