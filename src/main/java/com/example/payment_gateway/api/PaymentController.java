package com.example.payment_gateway.api;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.payment_gateway.api.dto.CreatePaymentRequest;
import com.example.payment_gateway.api.dto.PaymentResponse;
import com.example.payment_gateway.domain.Payment;
import com.example.payment_gateway.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
        Payment payment = service.create(request);
        return ResponseEntity
                .created(URI.create("/payments/" + payment.getId()))
                .body(toResponse(payment));
    }

    @GetMapping("/{id}")
    public PaymentResponse get(@PathVariable UUID id) {
        return toResponse(service.get(id));
    }

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

