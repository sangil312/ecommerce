package com.dev.core.ecommerce.api.controller.v1.payment.usecase;

import com.dev.core.ecommerce.service.order.OrderService;
import com.dev.core.ecommerce.service.payment.PaymentService;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.enums.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentUseCase {
    private final PaymentService paymentService;
    private final OrderService orderService;

    public Long createPayment(User user, String orderKey) {
        var order = orderService.findOrder(user, orderKey, OrderStatus.CREATED);
        return paymentService.createPayment(order);
    }

    public void failPayment(User user, String orderKey, String code, String message) {
        var order = orderService.findOrder(user, orderKey, OrderStatus.CREATED);
        paymentService.fail(order, code, message);
    }
}
