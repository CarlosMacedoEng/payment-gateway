package com.example.payment_gateway.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.payment_gateway.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
