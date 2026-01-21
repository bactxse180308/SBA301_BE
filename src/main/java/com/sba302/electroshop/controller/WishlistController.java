package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.Wishlist;
import com.sba302.electroshop.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ApiResponse<List<Wishlist>> getAll() {
        return ApiResponse.success(wishlistService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Wishlist> getById(@PathVariable Integer id) {
        return wishlistService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Wishlist not found"));
    }

    @PostMapping
    public ApiResponse<Wishlist> create(@RequestBody Wishlist wishlist) {
        return ApiResponse.success(wishlistService.save(wishlist));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        wishlistService.deleteById(id);
        return ApiResponse.success(null);
    }
}
