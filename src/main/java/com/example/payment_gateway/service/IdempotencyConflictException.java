package com.example.payment_gateway.service;

// Thrown when the same idempotency key is reused with a different request payload.
public class IdempotencyConflictException extends RuntimeException {

    // Signals a business rule violation to prevent inconsistent or duplicated operations.
    public IdempotencyConflictException(String key) {
        super("Idempotency-Key already used with a different request payload: " + key);
    }
}
