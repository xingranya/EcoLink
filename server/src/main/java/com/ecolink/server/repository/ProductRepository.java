package com.ecolink.server.repository;

import com.ecolink.server.domain.Product;
import com.ecolink.server.domain.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByIdAndStatus(Long id, ProductStatus status);
    Page<Product> findByNameContaining(String name, Pageable pageable);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    long countByStatus(ProductStatus status);
    long countByStockLessThanEqual(Integer stock);
    List<Product> findTop5ByOrderBySalesDescIdDesc();
}
