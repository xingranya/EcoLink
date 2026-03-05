package com.ecolink.server.dto.cart;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        String productImage,
        BigDecimal price,
        Integer quantity,
        Integer stock,
        BigDecimal subtotal
) {}
