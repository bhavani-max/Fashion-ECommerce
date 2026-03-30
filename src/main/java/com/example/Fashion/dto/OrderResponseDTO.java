package com.example.Fashion.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponseDTO {
    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private List<CartItemDTO> items;
}