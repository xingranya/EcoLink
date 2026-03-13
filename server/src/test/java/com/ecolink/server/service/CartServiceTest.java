package com.ecolink.server.service;

import com.ecolink.server.domain.CartItem;
import com.ecolink.server.domain.Product;
import com.ecolink.server.domain.User;
import com.ecolink.server.dto.cart.AddCartItemRequest;
import com.ecolink.server.exception.BizException;
import com.ecolink.server.repository.CartItemRepository;
import com.ecolink.server.repository.ProductRepository;
import com.ecolink.server.support.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private AuthService authService;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        TestSecurityContext.mockUser(1L);
    }

    @AfterEach
    void tearDown() {
        TestSecurityContext.clear();
    }

    @Test
    void addShouldCreateCartItemWhenStockEnough() {
        Product product = buildProduct(8L, 20);
        User currentUser = buildUser(1L);
        when(productRepository.findById(8L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId(1L, 8L)).thenReturn(Optional.empty());
        when(authService.getCurrentUserEntity()).thenReturn(currentUser);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.add(new AddCartItemRequest(8L, 2));

        ArgumentCaptor<CartItem> itemCaptor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).save(itemCaptor.capture());
        assertThat(itemCaptor.getValue().getQuantity()).isEqualTo(2);
        assertThat(itemCaptor.getValue().getUser().getId()).isEqualTo(1L);
    }

    @Test
    void addShouldRejectWhenRequestedQuantityExceedsStock() {
        when(productRepository.findById(8L)).thenReturn(Optional.of(buildProduct(8L, 1)));

        BizException exception = assertThrows(BizException.class, () -> cartService.add(new AddCartItemRequest(8L, 2)));

        assertThat(exception.getCode()).isEqualTo(4005);
    }

    @Test
    void addShouldMergeQuantityForSameProduct() {
        Product product = buildProduct(8L, 20);
        CartItem existing = new CartItem();
        existing.setUser(buildUser(1L));
        existing.setProduct(product);
        existing.setQuantity(2);
        when(productRepository.findById(8L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId(1L, 8L)).thenReturn(Optional.of(existing));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.add(new AddCartItemRequest(8L, 3));

        assertThat(existing.getQuantity()).isEqualTo(5);
        verify(cartItemRepository).save(existing);
    }

    private Product buildProduct(Long id, int stock) {
        Product product = new Product();
        product.setId(id);
        product.setName("生态苹果");
        product.setPrice(BigDecimal.valueOf(19.9));
        product.setStock(stock);
        product.setSales(0);
        return product;
    }

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("demo");
        user.setNickname("演示用户");
        return user;
    }
}
