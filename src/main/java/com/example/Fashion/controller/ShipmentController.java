package com.example.Fashion.controller;

import com.example.Fashion.dto.ShipmentDTO;
import com.example.Fashion.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for shipment tracking.
 */
@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ShipmentController {

    private final ShipmentService shipmentService;

    /** Get shipment info by order ID */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShipmentDTO> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(shipmentService.getByOrderId(orderId));
    }

    /** Track shipment by tracking number */
    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<ShipmentDTO> track(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(shipmentService.trackByNumber(trackingNumber));
    }
}