package com.dev.core.ecommerce.api.controller.v1.order;

import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.api.controller.v1.order.request.CreateOrderFromCartRequest;
import com.dev.core.ecommerce.api.controller.v1.order.request.CreateOrderRequest;
import com.dev.core.ecommerce.api.controller.v1.order.response.CreateOrderResponse;
import com.dev.core.ecommerce.api.controller.v1.response.ApiResponse;
import com.dev.core.ecommerce.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/v1/orders")
    public ApiResponse<CreateOrderResponse> create(
            User user,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        var orderKey = orderService.createOrder(user, request.toNewOrder(user));
        return ApiResponse.success(new CreateOrderResponse(orderKey));
    }

    @PostMapping("/v1/cart-orders")
    public ApiResponse<CreateOrderResponse> createFromCart(
            User user,
            @Valid @RequestBody CreateOrderFromCartRequest request
    ) {
        var orderKey = orderService.createFromCart(user, request.cartItemIds());
        return ApiResponse.success(new CreateOrderResponse(orderKey));
    }
}
