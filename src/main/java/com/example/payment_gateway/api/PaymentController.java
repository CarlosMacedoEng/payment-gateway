package com.example.payment_gateway.api;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.payment_gateway.api.dto.CreatePaymentRequest;
import com.example.payment_gateway.api.dto.PaymentResponse;
import com.example.payment_gateway.domain.Payment;
import com.example.payment_gateway.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Exposes REST endpoints related to payment operations.
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    // Injects the payment service to delegate business logic.
    private final PaymentService service;

    // Creates a new payment using an idempotency key to prevent duplicate processing.
    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                                  @Valid
                                                  @RequestBody CreatePaymentRequest request) {
        
        Payment payment = service.create(idempotencyKey, request);
        
        // Returns HTTP 201 with Location header pointing to the created resource.
        return ResponseEntity
            .created(URI.create("/payments/" + payment.getId()))
            .body(toResponse(payment));
    }

    // Retrieves a payment by its unique identifier.
    @GetMapping("/{id}")
    public PaymentResponse get(@PathVariable UUID id) {
        return toResponse(service.get(id));
    }

    // Maps domain Payment entity to an API response DTO.
    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getAmount(),
                p.getCurrency(),
                p.getCustomerId(),
                p.getStatus(),
                p.getCreatedAt()
        );
    }
}
