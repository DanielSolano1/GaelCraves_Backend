package com.gaelcraves.project3.GaelCravings_Backend.Entity;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY_FOR_PICKUP,
    PICKED_UP,
    DELIVERING,
    DELIVERED,
    CANCELLED,
    REFUNDED
}
