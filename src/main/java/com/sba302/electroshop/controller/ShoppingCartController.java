package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.AddToCartRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.CartResponse;
import com.sba302.electroshop.service.ShoppingCartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.toString() == #userId.toString()")
    public ApiResponse<CartResponse> getByUser(@PathVariable Integer userId) {
        return ApiResponse.success(shoppingCartService.getByUser(userId));
    }

    @PostMapping("/{userId}/items")
    @PreAuthorize("authentication.principal.toString() == #userId.toString()")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CartResponse> addItem(
            @PathVariable Integer userId,
            @Valid @RequestBody AddToCartRequest request) {
        return ApiResponse.success(shoppingCartService.addItem(userId, request));
    }

    @PatchMapping("/{userId}/items/{productId}")
    @PreAuthorize("authentication.principal.toString() == #userId.toString()")
    public ApiResponse<CartResponse> updateItemQuantity(
            @PathVariable Integer userId,
            @PathVariable Integer productId,
            @RequestParam Integer quantity) {
        return ApiResponse.success(shoppingCartService.updateItemQuantity(userId, productId, quantity));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    @PreAuthorize("authentication.principal.toString() == #userId.toString()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> removeItem(
            @PathVariable Integer userId,
            @PathVariable Integer productId) {
        shoppingCartService.removeItem(userId, productId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("authentication.principal.toString() == #userId.toString()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> clearCart(@PathVariable Integer userId) {
        shoppingCartService.clearCart(userId);
        return ApiResponse.success(null);
    }
}
