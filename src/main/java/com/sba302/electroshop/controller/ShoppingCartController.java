package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.ShoppingCart;
import com.sba302.electroshop.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shopping-carts")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ApiResponse<List<ShoppingCart>> getAll() {
        return ApiResponse.success(shoppingCartService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<ShoppingCart> getById(@PathVariable Integer id) {
        return shoppingCartService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "ShoppingCart not found"));
    }

    @PostMapping
    public ApiResponse<ShoppingCart> create(@RequestBody ShoppingCart shoppingCart) {
        return ApiResponse.success(shoppingCartService.save(shoppingCart));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        shoppingCartService.deleteById(id);
        return ApiResponse.success(null);
    }
}
