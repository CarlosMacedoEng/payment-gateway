package com.example.payment_gateway.repository;

import java.util.Optional;
import java.util.UUID;

import com.example.payment_gateway.domain.IdempotencyKey;

// Defines persistence operations for managing idempotency keys.
public interface IdempotencyKeyRepository {

    // Retrieves an idempotency key if it already exists.
    Optional<IdempotencyKey> findByKey(String idempotencyKey);

    /**
     * Attempts to insert a new idempotency record or returns the existing one.
     * Ensures consistent behavior for repeated requests with the same key.
     */
    IdempotencyKey insertOrGetExisting(String idempotencyKey, String requestHash, UUID paymentId);
}
