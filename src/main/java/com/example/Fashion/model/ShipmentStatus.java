package com.example.Fashion.model;

/** Lifecycle stages of a shipment */
public enum ShipmentStatus {
    PROCESSING,   // Order confirmed, preparing to ship
    SHIPPED,      // Handed to carrier
    IN_TRANSIT,   // On the way
    OUT_FOR_DELIVERY, // Last-mile delivery
    DELIVERED,    // Successfully delivered
    RETURNED      // Returned to sender
}