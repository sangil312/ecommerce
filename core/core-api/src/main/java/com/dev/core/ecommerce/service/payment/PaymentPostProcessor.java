package com.dev.core.ecommerce.service.payment;

import com.dev.core.ecommerce.domain.order.OrderItem;
import com.dev.core.ecommerce.repository.payment.PaymentRepository;
import com.dev.core.ecommerce.service.cart.CartWriter;
import com.dev.core.ecommerce.service.order.OrderReader;
import com.dev.core.ecommerce.support.error.ApiException;
import com.dev.core.ecommerce.support.error.ErrorType;
import com.dev.core.enums.order.OrderStatus;
import com.dev.core.enums.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentPostProcessor {
    private final PaymentRepository paymentRepository;
    private final OrderReader orderReader;
    private final CartWriter cartWriter;

    @Async
    public void processSuccess(Long userId, Long paymentId, String orderKey) {
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ApiException(ErrorType.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.SUCCESS) throw new ApiException(ErrorType.PAYMENT_INVALID_STATUS);

        var orderAndItems = orderReader.findOrderAndItems(userId, orderKey, OrderStatus.PAID);

        var productIds = orderAndItems.items().stream().map(OrderItem::getProductId).toList();

        cartWriter.deleteCartItemsByProduct(userId, productIds);
    }
}
