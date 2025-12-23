package com.dev.ecommerce.service.order;

import com.dev.ecommerce.common.auth.User;
import com.dev.ecommerce.domain.order.request.NewOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderWriter orderWriter;

    public String create(User user, NewOrder newOrder) {
        return orderWriter.create(user, newOrder);
    }
}
