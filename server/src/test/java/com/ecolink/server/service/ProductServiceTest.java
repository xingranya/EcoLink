package com.ecolink.server.service;

import com.ecolink.server.domain.Category;
import com.ecolink.server.domain.Product;
import com.ecolink.server.domain.enums.ProductStatus;
import com.ecolink.server.repository.CategoryRepository;
import com.ecolink.server.repository.ProductImageRepository;
import com.ecolink.server.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductImageRepository productImageRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void categoriesShouldReturnOnlyEnabledCategoryResponses() {
        Category fruit = buildCategory(1L, "新鲜瓜果", 1);
        Category vegetables = buildCategory(2L, "时令蔬菜", 2);
        when(categoryRepository.findByEnabledTrueOrderBySortAscIdAsc()).thenReturn(List.of(fruit, vegetables));

        var result = productService.categories();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("新鲜瓜果");
        assertThat(result.get(1).name()).isEqualTo("时令蔬菜");
    }

    @Test
    void listProductsShouldMapPageDataAndUseRequestedSort() {
        Product apple = buildProduct(10L, "高原苹果", BigDecimal.valueOf(39.9), 120, 36);
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(apple)));

        var result = productService.listProducts("苹果", 1L, BigDecimal.TEN, BigDecimal.valueOf(50), "price_desc", 1, 5);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).findAll(any(Specification.class), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();

        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().getOrderFor("price")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("price").isDescending()).isTrue();
        assertThat(result.list()).hasSize(1);
        assertThat(result.list().get(0).name()).isEqualTo("高原苹果");
    }

    private Category buildCategory(Long id, String name, int sort) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setSort(sort);
        category.setEnabled(true);
        return category;
    }

    private Product buildProduct(Long id, String name, BigDecimal price, int stock, int sales) {
        Product product = new Product();
        product.setId(id);
        product.setCategory(buildCategory(1L, "新鲜瓜果", 1));
        product.setName(name);
        product.setSubtitle("生态优选");
        product.setPrice(price);
        product.setStock(stock);
        product.setSales(sales);
        product.setStatus(ProductStatus.ON_SALE);
        product.setMainImage("https://example.com/apple.jpg");
        return product;
    }
}
