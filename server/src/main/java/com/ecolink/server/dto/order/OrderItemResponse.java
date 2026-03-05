package com.ecolink.server.dto.order;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        String productImage,
        BigDecimal salePrice,
        Integer quantity,
        BigDecimal subtotal
) {}
