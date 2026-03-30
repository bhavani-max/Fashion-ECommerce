package com.example.Fashion.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Tracks shipping status for a placed order.
 * Each order has one shipment record that is updated as the package moves.
 */
@Entity
@Table(name = "shipments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The order this shipment belongs to */
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** Simulated tracking number (e.g. SHIP-1001) */
    @Column(nullable = false, unique = true)
    private String trackingNumber;

    /** Carrier name — e.g. BlueDart, Delhivery, DTDC */
    private String carrier;

    /** Current status of the shipment */
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    /** Estimated delivery date as a string e.g. "2026-04-05" */
    private String estimatedDelivery;

    /** Latest location update */
    private String currentLocation;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}