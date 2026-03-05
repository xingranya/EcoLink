package com.ecolink.server.controller;

import com.ecolink.server.common.ApiResponse;
import com.ecolink.server.dto.cart.AddCartItemRequest;
import com.ecolink.server.dto.cart.CartResponse;
import com.ecolink.server.dto.cart.UpdateCartItemRequest;
import com.ecolink.server.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<CartResponse> list() {
        return ApiResponse.ok(cartService.list());
    }

    @PostMapping("/items")
    public ApiResponse<Void> add(@Valid @RequestBody AddCartItemRequest request) {
        cartService.add(request);
        return ApiResponse.okMessage("加入购物车成功");
    }

    @PutMapping("/items/{itemId}")
    public ApiResponse<Void> update(@PathVariable Long itemId, @Valid @RequestBody UpdateCartItemRequest request) {
        cartService.update(itemId, request);
        return ApiResponse.okMessage("购物车更新成功");
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<Void> delete(@PathVariable Long itemId) {
        cartService.delete(itemId);
        return ApiResponse.okMessage("删除成功");
    }
}
