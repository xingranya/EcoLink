package com.ecolink.server.service;

import com.ecolink.server.domain.Favorite;
import com.ecolink.server.domain.Product;
import com.ecolink.server.domain.User;
import com.ecolink.server.dto.favorite.FavoriteItemResponse;
import com.ecolink.server.exception.BizException;
import com.ecolink.server.repository.FavoriteRepository;
import com.ecolink.server.repository.ProductRepository;
import com.ecolink.server.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;

    public FavoriteService(FavoriteRepository favoriteRepository, ProductRepository productRepository, AuthService authService) {
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public List<FavoriteItemResponse> list() {
        Long userId = SecurityUtils.currentUserId();
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public void add(Long productId) {
        Long userId = SecurityUtils.currentUserId();
        Long safeProductId = Objects.requireNonNull(productId, "商品ID不能为空");
        if (favoriteRepository.existsByUserIdAndProductId(userId, safeProductId)) {
            return;
        }
        Product product = productRepository.findById(safeProductId).orElseThrow(() -> new BizException(4041, "商品不存在"));
        User user = authService.getCurrentUserEntity();
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void remove(Long productId) {
        Long userId = SecurityUtils.currentUserId();
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }

    private FavoriteItemResponse toResponse(Favorite favorite) {
        return new FavoriteItemResponse(
                favorite.getId(),
                favorite.getProduct().getId(),
                favorite.getProduct().getName(),
                favorite.getProduct().getMainImage(),
                favorite.getProduct().getPrice()
        );
    }
}
