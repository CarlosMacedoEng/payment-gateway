package com.example.payment_gateway.service;

import java.util.UUID;

// Thrown when a payment cannot be found for the given identifier.
public class PaymentNotFoundException extends RuntimeException {

    // Signals a missing resource to allow proper HTTP 404 handling at the API layer.
    public PaymentNotFoundException(UUID id) {
        super("Payment not found: " + id);
    }
}
