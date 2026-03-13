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
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        Specification<Product> specification = (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(root.get("name"), "%" + keyword.trim() + "%"));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), ProductStatus.valueOf(status)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Product> result = productRepository.findAll(specification, pageReq);
        Map<String, Object> data = Map.of(
                "content", result.getContent(),
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages(),
                "number", result.getNumber()
        );
        return ApiResponse.ok(data);
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> detail(@PathVariable long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BizException(4040, "商品不存在"));
        return ApiResponse.ok(product);
    }

    @PostMapping
    public ApiResponse<Product> create(@Valid @RequestBody ProductReq req) {
        Long categoryId = req.categoryId();
        if (categoryId == null) {
            throw new BizException(4000, "分类不能为空");
        }
        Category category = categoryRepository.findById(categoryId)
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
    public ApiResponse<Product> update(@PathVariable long id, @Valid @RequestBody ProductReq req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BizException(4040, "商品不存在"));
        if (req.categoryId() != null) {
            Long categoryId = req.categoryId();
            Category category = categoryRepository.findById(categoryId)
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
    public ApiResponse<Void> delete(@PathVariable long id) {
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
