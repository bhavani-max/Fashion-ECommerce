package com.example.Fashion.controller;

import com.example.Fashion.dto.ProductDTO;
import com.example.Fashion.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Returns personalized product recommendations for a user.
 */
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RecommendationController {

    private final RecommendationService recommendationService;

    /** Get up to 8 recommended products for a user */
    @GetMapping("/{userId}")
    public ResponseEntity<List<ProductDTO>> getRecommendations(@PathVariable Long userId) {
        return ResponseEntity.ok(recommendationService.getRecommendations(userId));
    }
}