package com.example.Fashion.controller;

import com.example.Fashion.dto.OrderRequestDTO;
import com.example.Fashion.dto.OrderResponseDTO;
import com.example.Fashion.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}/place")
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @PathVariable Long userId,
            @RequestBody OrderRequestDTO request) {
        return ResponseEntity.ok(orderService.placeOrder(userId, request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }
}