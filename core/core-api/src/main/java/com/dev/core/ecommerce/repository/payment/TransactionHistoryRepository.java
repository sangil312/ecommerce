package com.dev.core.ecommerce.repository.payment;

import com.dev.core.ecommerce.domain.payment.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
}
