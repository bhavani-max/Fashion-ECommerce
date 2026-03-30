package com.example.Fashion.controller;

import com.example.Fashion.dto.WishlistDTO;
import com.example.Fashion.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST endpoints for wishlist management.
 * All routes require authentication (JWT).
 */
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class WishlistController {

    private final WishlistService wishlistService;

    /** Get all wishlisted products for a user */
    @GetMapping("/{userId}")
    public ResponseEntity<List<WishlistDTO>> getWishlist(@PathVariable Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlist(userId));
    }

    /** Add a product to wishlist */
    @PostMapping("/{userId}/add")
    public ResponseEntity<WishlistDTO> addToWishlist(
            @PathVariable Long userId,
            @RequestBody Map<String, Long> body) {
        return ResponseEntity.ok(wishlistService.addToWishlist(userId, body.get("productId")));
    }

    /** Remove a product from wishlist */
    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        wishlistService.removeFromWishlist(userId, productId);
        return ResponseEntity.noContent().build();
    }

    /** Check if a product is wishlisted by a user */
    @GetMapping("/{userId}/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> isWishlisted(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(Map.of("wishlisted",
                wishlistService.isWishlisted(userId, productId)));
    }
}