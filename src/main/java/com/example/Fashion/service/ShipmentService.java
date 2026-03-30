package com.example.Fashion.service;

import com.example.Fashion.dto.ShipmentDTO;
import com.example.Fashion.model.*;
import com.example.Fashion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

/**
 * Simulates shipping carrier integration.
 * On order placement, a shipment is auto-created with a carrier and tracking number.
 * Supports tracking by orderId or trackingNumber.
 */
@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;

    /** Available simulated carriers */
    private static final List<String> CARRIERS = List.of("BlueDart", "Delhivery", "DTDC", "Ekart");

    /**
     * Called automatically when an order is placed.
     * Assigns a carrier and generates a tracking number.
     */
    @Transactional
    public ShipmentDTO createShipment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Pick a carrier based on orderId (deterministic simulation)
        String carrier = CARRIERS.get((int)(orderId % CARRIERS.size()));
        String trackingNumber = "SHIP-" + (1000 + orderId);
        String estimatedDelivery = LocalDate.now().plusDays(5).toString();

        Shipment shipment = Shipment.builder()
                .order(order)
                .trackingNumber(trackingNumber)
                .carrier(carrier)
                .status(ShipmentStatus.PROCESSING)
                .estimatedDelivery(estimatedDelivery)
                .currentLocation("Seller Warehouse")
                .build();

        return toDTO(shipmentRepository.save(shipment));
    }

    /** Get shipment details by order ID */
    public ShipmentDTO getByOrderId(Long orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Shipment not found for order " + orderId));
    }

    /** Track by tracking number */
    public ShipmentDTO trackByNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Tracking number not found: " + trackingNumber));
    }

    private ShipmentDTO toDTO(Shipment s) {
        return ShipmentDTO.builder()
                .id(s.getId())
                .orderId(s.getOrder().getId())
                .trackingNumber(s.getTrackingNumber())
                .carrier(s.getCarrier())
                .status(s.getStatus().name())
                .estimatedDelivery(s.getEstimatedDelivery())
                .currentLocation(s.getCurrentLocation())
                .build();
    }
}