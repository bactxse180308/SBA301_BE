package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.WishlistResponse;
import com.sba302.electroshop.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping("/{userId}")
    public ApiResponse<WishlistResponse> getByUser(@PathVariable Integer userId) {
        return ApiResponse.success(wishlistService.getByUser(userId));
    }

    @PostMapping("/{userId}/items/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WishlistResponse> addItem(
            @PathVariable Integer userId,
            @PathVariable Integer productId) {
        return ApiResponse.success(wishlistService.addItem(userId, productId));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> removeItem(
            @PathVariable Integer userId,
            @PathVariable Integer productId) {
        wishlistService.removeItem(userId, productId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> clearWishlist(@PathVariable Integer userId) {
        wishlistService.clearWishlist(userId);
        return ApiResponse.success(null);
    }

    @GetMapping("/{userId}/items/{productId}/check")
    public ApiResponse<Boolean> isProductInWishlist(
            @PathVariable Integer userId,
            @PathVariable Integer productId) {
        return ApiResponse.success(wishlistService.isProductInWishlist(userId, productId));
    }
}
