package com.ecolink.server.repository;

import com.ecolink.server.domain.Product;
import com.ecolink.server.domain.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByIdAndStatus(Long id, ProductStatus status);
}
