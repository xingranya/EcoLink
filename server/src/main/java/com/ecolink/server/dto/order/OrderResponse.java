package com.ecolink.server.dto.order;

import com.ecolink.server.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNo,
        OrderStatus status,
        BigDecimal totalAmount,
        String receiverName,
        String receiverPhone,
        String receiverAddress,
        LocalDateTime paidAt,
        LocalDateTime shippedAt,
        LocalDateTime completedAt,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {}
