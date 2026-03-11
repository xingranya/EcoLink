package com.ecolink.server.controller.admin;

import com.ecolink.server.common.ApiResponse;
import com.ecolink.server.domain.Category;
import com.ecolink.server.domain.Product;
import com.ecolink.server.domain.enums.ProductStatus;
import com.ecolink.server.exception.BizException;
import com.ecolink.server.repository.CategoryRepository;
import com.ecolink.server.repository.ProductRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 后台商品管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/products")
public class AdminProductController {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public AdminProductController(ProductRepository productRepository,
                                  CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        PageRequest pageReq = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Product> result;
        if (keyword != null && !keyword.isBlank()) {
            result = productRepository.findByNameContaining(keyword, pageReq);
        } else if (categoryId != null) {
            result = productRepository.findByCategoryId(categoryId, pageReq);
        } else {
            result = productRepository.findAll(pageReq);
        }
        Map<String, Object> data = Map.of(
                "content", result.getContent(),
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages(),
                "number", result.getNumber()
        );
        return ApiResponse.ok(data);
    }

    @PostMapping
    public ApiResponse<Product> create(@Valid @RequestBody ProductReq req) {
        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new BizException(4040, "分类不存在"));
        Product product = new Product();
        product.setCategory(category);
        product.setName(req.name());
        product.setSubtitle(req.subtitle());
        product.setPrice(req.price());
        product.setStock(req.stock() != null ? req.stock() : 0);
        product.setMainImage(req.mainImage());
        product.setDetail(req.detail());
        product.setStatus(req.status() != null ? ProductStatus.valueOf(req.status()) : ProductStatus.ON_SALE);
        return ApiResponse.ok(productRepository.save(product));
    }

    @PutMapping("/{id}")
    public ApiResponse<Product> update(@PathVariable Long id, @Valid @RequestBody ProductReq req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BizException(4040, "商品不存在"));
        if (req.categoryId() != null) {
            Category category = categoryRepository.findById(req.categoryId())
                    .orElseThrow(() -> new BizException(4040, "分类不存在"));
            product.setCategory(category);
        }
        product.setName(req.name());
        product.setSubtitle(req.subtitle());
        product.setPrice(req.price());
        if (req.stock() != null) product.setStock(req.stock());
        product.setMainImage(req.mainImage());
        product.setDetail(req.detail());
        if (req.status() != null) product.setStatus(ProductStatus.valueOf(req.status()));
        return ApiResponse.ok(productRepository.save(product));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    public record ProductReq(
            @NotNull Long categoryId,
            @NotBlank String name,
            String subtitle,
            @NotNull BigDecimal price,
            Integer stock,
            String mainImage,
            String detail,
            String status
    ) {}
}
