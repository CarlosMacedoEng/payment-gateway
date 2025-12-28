package com.example.payment_gateway.service;

import java.security.MessageDigest;

import org.springframework.stereotype.Component;

import com.example.payment_gateway.api.dto.CreatePaymentRequest;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

// Generates a deterministic hash of the request to support idempotency validation.
@Component
@RequiredArgsConstructor
public class RequestHasher {

    // Serializes the request into a stable JSON representation.
    private final ObjectMapper objectMapper;

    // Produces a SHA-256 hash to detect payload differences across repeated requests.
    public String hash(CreatePaymentRequest request) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(request);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(json);

            return bytesToHex(hash);
        } catch (Exception e) {
            // Fails fast to avoid inconsistent idempotency behavior.
            throw new IllegalStateException("Failed to hash request", e);
        }
    }

    // Converts raw hash bytes into a hexadecimal string for storage and comparison.
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
