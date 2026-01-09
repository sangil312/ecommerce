package com.dev.ecommerce.service.order;

import com.dev.ecommerce.common.EntityState;
import com.dev.ecommerce.common.error.ApiException;
import com.dev.ecommerce.common.auth.User;
import com.dev.ecommerce.common.error.ErrorType;
import com.dev.ecommerce.domain.order.Order;
import com.dev.ecommerce.domain.order.OrderItem;
import com.dev.ecommerce.domain.order.request.NewOrder;
import com.dev.ecommerce.domain.order.request.NewOrderItem;
import com.dev.ecommerce.domain.product.Product;
import com.dev.ecommerce.repository.order.OrderItemRepository;
import com.dev.ecommerce.repository.order.OrderRepository;
import com.dev.ecommerce.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class OrderWriter {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public String create(User user, NewOrder newOrder) {
        Set<Long> productIds = newOrder.items().stream()
                .map(NewOrderItem::productId)
                .collect(Collectors.toSet());

        Map<Long, Product> productMap = productRepository.findByIdInAndState(productIds, EntityState.ACTIVE)
                .stream()
                .collect(toMap(Product::getId, product -> product));

        validateNewOrderItem(productIds, productMap);

        BigDecimal totalPrice = calculateTotalPrice(newOrder.items(), productMap);

        Order savedOrder = orderRepository.save(Order.create(user.id(), totalPrice));

        List<OrderItem> orderItems = newOrder.items().stream()
                .map(item -> {
                    Product product = productMap.get(item.productId());
                    return OrderItem.create(
                            savedOrder.getId(),
                            product.getId(),
                            item.quantity(),
                            product.getName(),
                            product.getPrice(),
                            totalPrice
                    );
                })
                .toList();

        orderItemRepository.saveAll(orderItems);

        return savedOrder.getOrderKey();
    }

    private void validateNewOrderItem(Set<Long> orderProductIds, Map<Long, Product> productMap) {
        if (productMap.isEmpty()) throw new ApiException(ErrorType.PRODUCT_NOT_FOUND);
        if (!productMap.keySet().containsAll(orderProductIds)) throw new ApiException(ErrorType.PRODUCT_MISMATCH_IN_ORDER);
    }

    private BigDecimal calculateTotalPrice(List<NewOrderItem> newOrderItems, Map<Long, Product> productMap) {
        return newOrderItems.stream()
                .map(item ->
                        productMap.get(item.productId()).getPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
