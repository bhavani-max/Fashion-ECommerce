package com.example.Fashion.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Stores Razorpay payment details linked to an order.
 * Created when a Razorpay order is initiated and updated on payment capture.
 */
@Entity
@Table(name = "payments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The order this payment is for */
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** Razorpay order ID returned when creating a Razorpay order */
    private String razorpayOrderId;

    /** Razorpay payment ID returned after successful payment */
    private String razorpayPaymentId;

    /** Razorpay signature for verification */
    private String razorpaySignature;

    /** Amount in INR */
    private BigDecimal amount;

    /** CREATED, PAID, FAILED */
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}