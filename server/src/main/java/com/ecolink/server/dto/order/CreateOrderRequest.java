package com.ecolink.server.dto.order;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotNull(message = "地址不能为空")
        Long addressId,
        List<Long> cartItemIds
) {}
