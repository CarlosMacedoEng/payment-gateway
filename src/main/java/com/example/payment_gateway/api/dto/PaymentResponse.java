package com.example.payment_gateway.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.example.payment_gateway.domain.PaymentStatus;

public record PaymentResponse(
        UUID id,
        BigDecimal amount,
        String currency,
        String customerId,
        PaymentStatus status,
        Instant createdAt
) {}

