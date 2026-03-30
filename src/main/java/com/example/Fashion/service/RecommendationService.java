package com.example.Fashion.service;

import com.example.Fashion.dto.ProductDTO;
import com.example.Fashion.model.*;
import com.example.Fashion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides personalized product recommendations for a user.
 * Strategy:
 *  1. Find categories the user has ordered from before.
 *  2. Return products from those categories that the user hasn't bought yet.
 *  3. Fall back to top-selling products if no order history exists.
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    /**
     * Returns up to 8 recommended products for the given user.
     * Based on purchase history — same category, different products.
     */
    public List<ProductDTO> getRecommendations(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        if (orders.isEmpty()) {
            // New user — return newest 8 products as default recommendations
            return productRepository.findAll().stream()
                    .sorted(Comparator.comparing(Product::getCreatedAt,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(8)
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }

        // Collect category IDs from past orders
        Set<Long> purchasedCategoryIds = orders.stream()
                .flatMap(o -> o.getOrderItems().stream())
                .map(oi -> oi.getProduct().getCategory())
                .filter(Objects::nonNull)
                .map(Category::getId)
                .collect(Collectors.toSet());

        // Collect product IDs already purchased (to exclude them)
        Set<Long> purchasedProductIds = orders.stream()
                .flatMap(o -> o.getOrderItems().stream())
                .map(oi -> oi.getProduct().getId())
                .collect(Collectors.toSet());

        // Find products in same categories not yet purchased
        List<ProductDTO> recommended = productRepository.findAll().stream()
                .filter(p -> p.getCategory() != null
                        && purchasedCategoryIds.contains(p.getCategory().getId())
                        && !purchasedProductIds.contains(p.getId()))
                .limit(8)
                .map(this::toDTO)
                .collect(Collectors.toList());

        // If not enough results, pad with other products
        if (recommended.size() < 4) {
            List<ProductDTO> extras = productRepository.findAll().stream()
                    .filter(p -> !purchasedProductIds.contains(p.getId()))
                    .limit(8 - recommended.size())
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            recommended.addAll(extras);
        }

        return recommended;
    }

    private ProductDTO toDTO(Product p) {
        return ProductDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stockQuantity(p.getStockQuantity())
                .imageUrl(p.getImageUrl())
                .brand(p.getBrand())
                .size(p.getSize())
                .color(p.getColor())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .build();
    }
}