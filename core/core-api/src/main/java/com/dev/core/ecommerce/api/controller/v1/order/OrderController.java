package com.dev.core.ecommerce.api.controller.v1.order;

import com.dev.core.ecommerce.api.controller.v1.order.response.OrderCheckoutResponse;
import com.dev.core.ecommerce.api.controller.v1.order.usecase.OrderUseCase;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.api.controller.v1.order.request.CreateOrderFromCartRequest;
import com.dev.core.ecommerce.api.controller.v1.order.request.CreateOrderRequest;
import com.dev.core.ecommerce.api.controller.v1.order.response.CreateOrderResponse;
import com.dev.core.ecommerce.api.controller.v1.response.ApiResponse;
import com.dev.core.ecommerce.service.order.OrderService;
import com.dev.core.enums.order.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderUseCase orderUseCase;

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
        var orderKey = orderUseCase.createFromCart(user, request.cartItemIds());
        return ApiResponse.success(new CreateOrderResponse(orderKey));
    }

    @GetMapping("/v1/orders/{orderKey}/checkout")
    public ApiResponse<OrderCheckoutResponse> findCheckoutOrder(
            User user,
            @PathVariable String orderKey
    ) {
        var result = orderService.findOrderAndItems(user, orderKey, OrderStatus.CREATED);
        return ApiResponse.success(OrderCheckoutResponse.of(result));
    }
}
