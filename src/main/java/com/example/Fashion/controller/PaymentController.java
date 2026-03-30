package com.example.Fashion.controller;

import com.example.Fashion.dto.PaymentVerifyDTO;
import com.example.Fashion.dto.RazorpayOrderDTO;
import com.example.Fashion.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Handles Razorpay payment flow.
 * Step 1: POST /create/{orderId} → returns Razorpay order details to frontend
 * Step 2: POST /verify → verifies payment signature and confirms order
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    private final RazorpayService razorpayService;

    /** Create a Razorpay order for the given app order */
    @PostMapping("/create/{orderId}")
    public ResponseEntity<RazorpayOrderDTO> createPayment(@PathVariable Long orderId) throws Exception {
        return ResponseEntity.ok(razorpayService.createRazorpayOrder(orderId));
    }

    /** Verify payment signature after user completes payment */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyPayment(@RequestBody PaymentVerifyDTO dto) {
        razorpayService.verifyAndCapture(dto);
        return ResponseEntity.ok(Map.of("status", "Payment successful"));
    }
}