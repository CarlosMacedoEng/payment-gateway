package com.example.payment_gateway.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.payment_gateway.api.dto.CreatePaymentRequest;
import com.example.payment_gateway.domain.IdempotencyKey;
import com.example.payment_gateway.domain.Payment;
import com.example.payment_gateway.repository.IdempotencyKeyRepository;
import com.example.payment_gateway.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

// Coordinates payment creation while enforcing idempotency and transactional consistency.
@Service
@RequiredArgsConstructor
public class PaymentService {

    // Handles persistence of payment entities.
    private final PaymentRepository paymentRepository;

    // Manages idempotency keys to prevent duplicated processing.
    private final IdempotencyKeyRepository idempotencyRepository;

    // Generates a deterministic hash to detect payload differences.
    private final RequestHasher requestHasher;

    // Executes the full payment creation flow atomically.
    @Transactional
    public Payment create(String idempotencyKey, CreatePaymentRequest request) {

        // Hashes the request to validate idempotency payload consistency.
        String requestHash = requestHasher.hash(request);

        // Fast path: validates payload if the idempotency key already exists.
        idempotencyRepository.findByKey(idempotencyKey).ifPresent(existing -> {
            validateSamePayload(existing, requestHash);
            // If hashes match, the existing payment will be returned later.
        });

        // Loads existing idempotency record to return the associated payment.
        IdempotencyKey existing = idempotencyRepository.findByKey(idempotencyKey).orElse(null);
        if (existing != null) {
            validateSamePayload(existing, requestHash);
            return paymentRepository.findById(existing.getPaymentId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Idempotency record exists but payment not found. paymentId=" + existing.getPaymentId()
                    ));
        }

        // Creates a new payment when no idempotency key is present.
        Payment payment = paymentRepository.save(Payment.create(request));

        // Attempts to persist the idempotency key, handling concurrent insert races.
        IdempotencyKey savedOrExisting = idempotencyRepository
                .insertOrGetExisting(idempotencyKey, requestHash, payment.getId());

        // If another transaction won the race, returns the existing payment safely.
        if (!savedOrExisting.getPaymentId().equals(payment.getId())) {
            validateSamePayload(savedOrExisting, requestHash);

            return paymentRepository.findById(savedOrExisting.getPaymentId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Idempotency record exists but payment not found. paymentId=" + savedOrExisting.getPaymentId()
                    ));
        }

        // Successful creation path: returns the newly created payment.
        return payment;
    }

    // Retrieves a payment by id with read-only transactional guarantees.
    @Transactional(readOnly = true)
    public Payment get(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    // Ensures the same idempotency key is not reused with a different request payload.
    private void validateSamePayload(IdempotencyKey existing, String requestHash) {
        if (!existing.getRequestHash().equals(requestHash)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Idempotency-Key reuse with different payload"
            );
        }
    }
}
