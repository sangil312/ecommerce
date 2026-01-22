package com.dev.core.ecommerce.service.order;

import com.dev.core.ecommerce.common.auth.User;
import com.dev.core.ecommerce.service.order.request.NewOrder;
import com.dev.core.ecommerce.service.cart.CartReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderWriter orderWriter;
    private final CartReader cartReader;

    public String create(User user, NewOrder newOrder) {
        return orderWriter.create(user, newOrder);
    }

    public String createFromCart(User user, Collection<Long> cartItemIds) {
        var cart = cartReader.find(user, cartItemIds);
        return create(user, cart.toNewOrder());
    }
}
