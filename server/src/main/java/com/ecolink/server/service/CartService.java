package com.ecolink.server.service;

import com.ecolink.server.domain.CartItem;
import com.ecolink.server.domain.Product;
import com.ecolink.server.dto.cart.*;
import com.ecolink.server.exception.BizException;
import com.ecolink.server.repository.CartItemRepository;
import com.ecolink.server.repository.ProductRepository;
import com.ecolink.server.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository, AuthService authService) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.authService = authService;
    }

    public CartResponse list() {
        Long userId = SecurityUtils.currentUserId();
        List<CartItem> items = cartItemRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        List<CartItemResponse> rows = items.stream().map(this::toResponse).toList();
        BigDecimal total = rows.stream().map(CartItemResponse::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(rows, total);
    }

    @Transactional
    public void add(AddCartItemRequest request) {
        Long userId = SecurityUtils.currentUserId();
        Product product = productRepository.findById(request.productId()).orElseThrow(() -> new BizException(4041, "商品不存在"));
        if (request.quantity() > product.getStock()) {
            throw new BizException(4005, "库存不足");
        }
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, request.productId()).orElseGet(() -> {
            CartItem ci = new CartItem();
            ci.setUser(authService.getCurrentUserEntity());
            ci.setProduct(product);
            ci.setQuantity(0);
            return ci;
        });
        int target = item.getQuantity() + request.quantity();
        if (target > product.getStock()) {
            throw new BizException(4005, "库存不足");
        }
        item.setQuantity(target);
        cartItemRepository.save(item);
    }

    @Transactional
    public void update(Long itemId, UpdateCartItemRequest request) {
        Long userId = SecurityUtils.currentUserId();
        CartItem item = cartItemRepository.findByIdAndUserId(itemId, userId).orElseThrow(() -> new BizException(4043, "购物车条目不存在"));
        if (request.quantity() > item.getProduct().getStock()) {
            throw new BizException(4005, "库存不足");
        }
        item.setQuantity(request.quantity());
        cartItemRepository.save(item);
    }

    @Transactional
    public void delete(Long itemId) {
        Long userId = SecurityUtils.currentUserId();
        CartItem item = cartItemRepository.findByIdAndUserId(itemId, userId).orElseThrow(() -> new BizException(4043, "购物车条目不存在"));
        cartItemRepository.delete(item);
    }

    public List<CartItem> findItemsForCurrentUser(List<Long> itemIds) {
        Long userId = SecurityUtils.currentUserId();
        if (itemIds == null || itemIds.isEmpty()) {
            return cartItemRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        }
        return cartItemRepository.findByIdInAndUserId(itemIds, userId);
    }

    @Transactional
    public void removeItems(List<CartItem> items) {
        cartItemRepository.deleteAll(items);
    }

    private CartItemResponse toResponse(CartItem item) {
        BigDecimal subtotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getMainImage(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getProduct().getStock(),
                subtotal
        );
    }
}
