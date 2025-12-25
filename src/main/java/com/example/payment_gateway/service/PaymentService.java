package com.example.payment_gateway.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.payment_gateway.api.dto.CreatePaymentRequest;
import com.example.payment_gateway.domain.Payment;
import com.example.payment_gateway.domain.PaymentStatus;
import com.example.payment_gateway.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;

    @Transactional
    public Payment create(CreatePaymentRequest request) {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .amount(request.amount())
                .currency(request.currency().trim().toUpperCase())
                .customerId(request.customerId().trim())
                .status(PaymentStatus.CREATED)
                .createdAt(Instant.now())
                .build();

        return repository.save(payment);
    }

    @Transactional(readOnly = true)
    public Payment get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }
}

