package com.dev.ecommerce.repository.payment;

import com.dev.ecommerce.domain.payment.Payment;
import com.dev.ecommerce.domain.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByOrderIdAndStatus(Long id, PaymentStatus status);
    Optional<Payment> findByOrderId(Long id);
}
