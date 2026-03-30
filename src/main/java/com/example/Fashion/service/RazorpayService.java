package com.example.Fashion.service;

import com.example.Fashion.dto.PaymentVerifyDTO;
import com.example.Fashion.dto.RazorpayOrderDTO;
import com.example.Fashion.model.*;
import com.example.Fashion.repository.*;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Handles Razorpay payment flow:
 * 1. createOrder() — creates a Razorpay order and returns details to frontend
 * 2. verifyAndCapture() — verifies HMAC signature and marks payment as PAID
 */
@Service
@RequiredArgsConstructor
public class RazorpayService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    /**
     * Creates a Razorpay order for the given app order.
     * Amount is converted to paise (INR × 100).
     */
    @Transactional
    public RazorpayOrderDTO createRazorpayOrder(Long appOrderId) throws RazorpayException {
        Order order = orderRepository.findById(appOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        // Convert INR to paise
        int amountInPaise = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).intValue();

        JSONObject options = new JSONObject();
        options.put("amount", amountInPaise);
        options.put("currency", "INR");
        options.put("receipt", "receipt_order_" + appOrderId);

        com.razorpay.Order razorpayOrder = client.orders.create(options);
        String razorpayOrderId = razorpayOrder.get("id");

        // Persist payment record
        Payment payment = Payment.builder()
                .order(order)
                .razorpayOrderId(razorpayOrderId)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.CREATED)
                .build();
        paymentRepository.save(payment);

        return RazorpayOrderDTO.builder()
                .razorpayOrderId(razorpayOrderId)
                .amount(order.getTotalAmount())
                .currency("INR")
                .keyId(keyId)
                .appOrderId(appOrderId)
                .build();
    }

    /**
     * Verifies the Razorpay HMAC signature and marks the payment as PAID.
     * Throws RuntimeException if signature is invalid (possible tampering).
     */
    @Transactional
    public void verifyAndCapture(PaymentVerifyDTO dto) {
        // HMAC-SHA256 signature verification
        String payload = dto.getRazorpayOrderId() + "|" + dto.getRazorpayPaymentId();
        if (!verifySignature(payload, dto.getRazorpaySignature())) {
            throw new RuntimeException("Payment signature verification failed");
        }

        Payment payment = paymentRepository.findByRazorpayOrderId(dto.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment record not found"));

        payment.setRazorpayPaymentId(dto.getRazorpayPaymentId());
        payment.setRazorpaySignature(dto.getRazorpaySignature());
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // Update order status to CONFIRMED after payment
        Order order = payment.getOrder();
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }

    /** HMAC-SHA256 signature verification using Razorpay key secret */
    private boolean verifySignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString().equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}