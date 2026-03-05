package com.ecolink.server.controller;

import com.ecolink.server.common.ApiResponse;
import com.ecolink.server.dto.order.CreateOrderRequest;
import com.ecolink.server.dto.order.OrderResponse;
import com.ecolink.server.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.ok(orderService.createOrder(request));
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> list() {
        return ApiResponse.ok(orderService.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok(orderService.detail(id));
    }

    @PostMapping("/{id}/pay")
    public ApiResponse<OrderResponse> pay(@PathVariable Long id) {
        return ApiResponse.ok(orderService.pay(id));
    }
}
