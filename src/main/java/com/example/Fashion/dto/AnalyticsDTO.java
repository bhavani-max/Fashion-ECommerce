package com.example.Fashion.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

/** Aggregated analytics data returned to the admin dashboard */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AnalyticsDTO {

    /** Total revenue across all PAID orders */
    private BigDecimal totalRevenue;

    /** Total number of orders placed */
    private long totalOrders;

    /** Total number of registered customers */
    private long totalCustomers;

    /** Total number of products in the catalog */
    private long totalProducts;

    /** Revenue per category */
    private List<CategoryRevenueDTO> revenueByCategory;

    /** Top 5 best-selling products */
    private List<TopProductDTO> topProducts;

    /** Orders per status (PENDING, CONFIRMED, etc.) */
    private List<OrderStatusCountDTO> ordersByStatus;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CategoryRevenueDTO {
        private String category;
        private BigDecimal revenue;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class TopProductDTO {
        private String productName;
        private long totalSold;
        private BigDecimal revenue;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class OrderStatusCountDTO {
        private String status;
        private long count;
    }
}