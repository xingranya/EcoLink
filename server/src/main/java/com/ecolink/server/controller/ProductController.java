package com.ecolink.server.controller;

import com.ecolink.server.common.ApiResponse;
import com.ecolink.server.common.PageResult;
import com.ecolink.server.dto.product.CategoryResponse;
import com.ecolink.server.dto.product.ProductDetailResponse;
import com.ecolink.server.dto.product.ProductItemResponse;
import com.ecolink.server.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> categories() {
        return ApiResponse.ok(productService.categories());
    }

    @GetMapping("/products")
    public ApiResponse<PageResult<ProductItemResponse>> products(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "comprehensive") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ApiResponse.ok(productService.listProducts(keyword, categoryId, minPrice, maxPrice, sort, page, size));
    }

    @GetMapping("/products/{id}")
    public ApiResponse<ProductDetailResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok(productService.productDetail(id));
    }
}
