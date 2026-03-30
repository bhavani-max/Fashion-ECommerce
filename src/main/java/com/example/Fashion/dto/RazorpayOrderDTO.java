package com.example.Fashion.dto;

import lombok.*;
import java.math.BigDecimal;

/** Sent to frontend to initialize Razorpay checkout popup */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RazorpayOrderDTO {
    private String razorpayOrderId;  // e.g. order_xxxx
    private BigDecimal amount;        // in INR
    private String currency;          // INR
    private String keyId;             // public key shown to frontend
    private Long appOrderId;          // our internal order id
}