package com.ecolink.server.controller;

import com.ecolink.server.common.ApiResponse;
import com.ecolink.server.dto.favorite.FavoriteItemResponse;
import com.ecolink.server.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ApiResponse<List<FavoriteItemResponse>> list() {
        return ApiResponse.ok(favoriteService.list());
    }

    @PostMapping("/{productId}")
    public ApiResponse<Void> add(@PathVariable Long productId) {
        favoriteService.add(productId);
        return ApiResponse.okMessage("收藏成功");
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> remove(@PathVariable Long productId) {
        favoriteService.remove(productId);
        return ApiResponse.okMessage("取消收藏成功");
    }
}
