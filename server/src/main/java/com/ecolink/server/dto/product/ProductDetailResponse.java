package com.ecolink.server.dto.product;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String name,
        String subtitle,
        BigDecimal price,
        Integer stock,
        Integer sales,
        String detail,
        String mainImage,
        List<String> images
) {}
