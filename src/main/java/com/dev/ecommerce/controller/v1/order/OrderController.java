package com.dev.ecommerce.controller.v1.order;

import com.dev.ecommerce.controller.v1.order.request.CreateOrderFromCartRequest;
import com.dev.ecommerce.controller.v1.order.request.CreateOrderRequest;
import com.dev.ecommerce.controller.v1.order.response.CreateOrderResponse;
import com.dev.ecommerce.controller.v1.response.ApiResponse;
import com.dev.ecommerce.common.auth.User;
import com.dev.ecommerce.service.cart.CartService;
import com.dev.ecommerce.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;

    @PostMapping("/api/orders")
    public ApiResponse<CreateOrderResponse> create(
            User user,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        var orderKey = orderService.create(user, request.toNewOrder(user));
        return ApiResponse.success(new CreateOrderResponse(orderKey));
    }

    @PostMapping("/api/cart-orders")
    public ApiResponse<CreateOrderResponse> createFromCart(
            User user,
            @Valid @RequestBody CreateOrderFromCartRequest request
    ) {
        var cart = cartService.find(user, request.cartItemIds());
        var orderKey = orderService.create(user, cart.toNewOrder());
        return ApiResponse.success(new CreateOrderResponse(orderKey));
    }
}
