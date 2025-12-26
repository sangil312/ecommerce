package com.dev.ecommerce.repository;

import com.dev.ecommerce.domain.payment.Payment;
import com.dev.ecommerce.domain.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByOrderIdAndStatus(Long id, PaymentStatus status);
}
