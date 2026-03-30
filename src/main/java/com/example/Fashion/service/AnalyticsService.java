package com.example.Fashion.service;

import com.example.Fashion.dto.AnalyticsDTO;
import com.example.Fashion.model.*;
import com.example.Fashion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Computes aggregated sales analytics for the admin dashboard.
 * Covers: total revenue, orders, customers, top products, revenue by category.
 */
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * Builds the full analytics report.
     * Only counts CONFIRMED, SHIPPED, or DELIVERED orders as revenue.
     */
    public AnalyticsDTO getAnalytics() {
        List<Order> allOrders = orderRepository.findAll();
        List<Order> paidOrders = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.CONFIRMED
                        || o.getStatus() == OrderStatus.SHIPPED
                        || o.getStatus() == OrderStatus.DELIVERED)
                .collect(Collectors.toList());

        // Total revenue
        BigDecimal totalRevenue = paidOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Orders by status count
        List<AnalyticsDTO.OrderStatusCountDTO> ordersByStatus = allOrders.stream()
                .collect(Collectors.groupingBy(o -> o.getStatus().name(), Collectors.counting()))
                .entrySet().stream()
                .map(e -> new AnalyticsDTO.OrderStatusCountDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        // Revenue by category
        Map<String, BigDecimal> categoryRevenue = new LinkedHashMap<>();
        paidOrders.forEach(order -> order.getOrderItems().forEach(item -> {
            String cat = item.getProduct().getCategory() != null
                    ? item.getProduct().getCategory().getName() : "Uncategorized";
            BigDecimal lineTotal = item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            categoryRevenue.merge(cat, lineTotal, BigDecimal::add);
        }));

        List<AnalyticsDTO.CategoryRevenueDTO> revenueByCategory = categoryRevenue.entrySet().stream()
                .map(e -> new AnalyticsDTO.CategoryRevenueDTO(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(AnalyticsDTO.CategoryRevenueDTO::getRevenue).reversed())
                .collect(Collectors.toList());

        // Top 5 products by quantity sold
        Map<String, long[]> productStats = new LinkedHashMap<>();
        paidOrders.forEach(order -> order.getOrderItems().forEach(item -> {
            String name = item.getProduct().getName();
            productStats.computeIfAbsent(name, k -> new long[]{0, 0});
            productStats.get(name)[0] += item.getQuantity();
            productStats.get(name)[1] += item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())).longValue();
        }));

        List<AnalyticsDTO.TopProductDTO> topProducts = productStats.entrySet().stream()
                .map(e -> new AnalyticsDTO.TopProductDTO(
                        e.getKey(),
                        e.getValue()[0],
                        BigDecimal.valueOf(e.getValue()[1])))
                .sorted(Comparator.comparing(AnalyticsDTO.TopProductDTO::getTotalSold).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return AnalyticsDTO.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(allOrders.size())
                .totalCustomers(userRepository.count())
                .totalProducts(productRepository.count())
                .revenueByCategory(revenueByCategory)
                .topProducts(topProducts)
                .ordersByStatus(ordersByStatus)
                .build();
    }
}