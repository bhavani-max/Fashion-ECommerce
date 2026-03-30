package com.example.Fashion.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ShipmentDTO {
    private Long id;
    private Long orderId;
    private String trackingNumber;
    private String carrier;
    private String status;
    private String estimatedDelivery;
    private String currentLocation;
}