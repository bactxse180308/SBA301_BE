package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.AddToCartRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.CartResponse;
import com.sba302.electroshop.service.ShoppingCartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @GetMapping("/{userId}")
    public ApiResponse<CartResponse> getByUser(@PathVariable Integer userId) {
        return ApiResponse.success(shoppingCartService.getByUser(userId));
    }

    @PostMapping("/{userId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CartResponse> addItem(
            @PathVariable Integer userId,
            @Valid @RequestBody AddToCartRequest request) {
        return ApiResponse.success(shoppingCartService.addItem(userId, request));
    }

    @PatchMapping("/{userId}/items/{productId}")
    public ApiResponse<CartResponse> updateItemQuantity(
            @PathVariable Integer userId,
            @PathVariable Integer productId,
            @RequestParam Integer quantity) {
        return ApiResponse.success(shoppingCartService.updateItemQuantity(userId, productId, quantity));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> removeItem(
            @PathVariable Integer userId,
            @PathVariable Integer productId) {
        shoppingCartService.removeItem(userId, productId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> clearCart(@PathVariable Integer userId) {
        shoppingCartService.clearCart(userId);
        return ApiResponse.success(null);
    }
}
