package com.dev.ecommerce.controller.order;

import com.dev.ecommerce.controller.order.request.CreateOrderFromCartRequest;
import com.dev.ecommerce.controller.order.request.CreateOrderRequest;
import com.dev.ecommerce.controller.order.response.CreateOrderResponse;
import com.dev.ecommerce.controller.response.ApiResponse;
import com.dev.ecommerce.domain.User;
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

    @PostMapping("/api/orders")
    public ApiResponse<CreateOrderResponse> create(
            User user,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        var orderKey = orderService.create(user, request.toNewOrder(user));
        return ApiResponse.success(new CreateOrderResponse(orderKey));
    }

    @PostMapping("/api/cart-orders")
    public ApiResponse<?> createFromCart(
            User user,
            @Valid @RequestBody CreateOrderFromCartRequest request
    ) {

        return ApiResponse.success(null);
    }
}
