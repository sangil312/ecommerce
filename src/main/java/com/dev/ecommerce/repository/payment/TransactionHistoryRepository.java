package com.dev.ecommerce.repository.payment;

import com.dev.ecommerce.domain.payment.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
}
