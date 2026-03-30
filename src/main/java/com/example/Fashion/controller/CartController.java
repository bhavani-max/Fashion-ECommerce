package com.example.Fashion.controller;

import com.example.Fashion.dto.CartItemDTO;
import com.example.Fashion.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemDTO>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<List<CartItemDTO>> addToCart(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> body) {
        Long productId = Long.valueOf(((Number) body.get("productId")).longValue());
        int quantity = ((Number) body.get("quantity")).intValue();
        return ResponseEntity.ok(cartService.addToCart(userId, productId, quantity));
    }

    @DeleteMapping("/{userId}/remove/{cartItemId}")
    public ResponseEntity<List<CartItemDTO>> removeFromCart(
            @PathVariable Long userId,
            @PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.removeFromCart(userId, cartItemId));
    }
}