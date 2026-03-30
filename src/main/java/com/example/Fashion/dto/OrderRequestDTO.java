package com.example.Fashion.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderRequestDTO {
    private String shippingAddress;
    private String paymentMethod;
}