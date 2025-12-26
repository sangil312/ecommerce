package com.dev.ecommerce.service.payment;

import com.dev.ecommerce.common.auth.User;
import com.dev.ecommerce.domain.order.Order;
import com.dev.ecommerce.domain.order.OrderStatus;
import com.dev.ecommerce.service.order.OrderReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentWriter paymentWriter;
    private final OrderReader orderReader;

    public Long create(User user, String orderKey) {
        Order order = orderReader.find(user, orderKey, OrderStatus.CREATED);
        return paymentWriter.create(order);
    }
}
