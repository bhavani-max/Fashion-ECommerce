package com.example.Fashion.dto;

import lombok.*;

/** Payload from frontend after Razorpay payment completes */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentVerifyDTO {
    private Long appOrderId;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}