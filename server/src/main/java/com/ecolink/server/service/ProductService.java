package com.ecolink.server.service;

import com.ecolink.server.common.PageResult;
import com.ecolink.server.domain.Category;
import com.ecolink.server.domain.Product;
import com.ecolink.server.domain.enums.ProductStatus;
import com.ecolink.server.dto.product.CategoryResponse;
import com.ecolink.server.dto.product.ProductDetailResponse;
import com.ecolink.server.dto.product.ProductItemResponse;
import com.ecolink.server.exception.BizException;
import com.ecolink.server.repository.CategoryRepository;
import com.ecolink.server.repository.ProductImageRepository;
import com.ecolink.server.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository, ProductImageRepository productImageRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    public List<CategoryResponse> categories() {
        return categoryRepository.findByEnabledTrueOrderBySortAscIdAsc()
                .stream().map(c -> new CategoryResponse(c.getId(), c.getName())).toList();
    }

    public PageResult<ProductItemResponse> listProducts(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String sort, int page, int size) {
        Sort sortObj = buildSort(sort);
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), ProductStatus.ON_SALE));
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("name"), like),
                        cb.like(root.get("subtitle"), like)
                ));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Product> result = productRepository.findAll(spec, PageRequest.of(Math.max(page, 1) - 1, Math.max(size, 1), sortObj));
        List<ProductItemResponse> list = result.getContent().stream().map(this::toItem).toList();
        return new PageResult<>(list, page, size, result.getTotalElements());
    }

    public ProductDetailResponse productDetail(Long id) {
        Product product = productRepository.findByIdAndStatus(id, ProductStatus.ON_SALE)
                .orElseThrow(() -> new BizException(4041, "商品不存在或已下架"));
        List<String> images = productImageRepository.findByProductIdOrderBySortAscIdAsc(id)
                .stream().map(it -> it.getImageUrl()).toList();
        if (images.isEmpty() && product.getMainImage() != null) {
            images = List.of(product.getMainImage());
        }
        return new ProductDetailResponse(
                product.getId(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getName(),
                product.getSubtitle(),
                product.getPrice(),
                product.getStock(),
                product.getSales(),
                product.getDetail(),
                product.getMainImage(),
                images
        );
    }

    private ProductItemResponse toItem(Product product) {
        Category category = product.getCategory();
        return new ProductItemResponse(
                product.getId(),
                category.getId(),
                category.getName(),
                product.getName(),
                product.getSubtitle(),
                product.getPrice(),
                product.getStock(),
                product.getSales(),
                product.getMainImage()
        );
    }

    private Sort buildSort(String sort) {
        if (sort == null || sort.isBlank() || "comprehensive".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Order.desc("sales"), Sort.Order.desc("id"));
        }
        return switch (sort) {
            case "price_asc" -> Sort.by(Sort.Order.asc("price"), Sort.Order.desc("id"));
            case "price_desc" -> Sort.by(Sort.Order.desc("price"), Sort.Order.desc("id"));
            case "latest" -> Sort.by(Sort.Order.desc("id"));
            default -> Sort.by(Sort.Order.desc("sales"), Sort.Order.desc("id"));
        };
    }
}
