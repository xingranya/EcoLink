package com.ecolink.server.dto.product;

import java.math.BigDecimal;

public record ProductItemResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String name,
        String subtitle,
        BigDecimal price,
        Integer stock,
        Integer sales,
        String mainImage
) {}
