package com.ecolink.server.dto.favorite;

import java.math.BigDecimal;

public record FavoriteItemResponse(
        Long id,
        Long productId,
        String productName,
        String productImage,
        BigDecimal price
) {}
