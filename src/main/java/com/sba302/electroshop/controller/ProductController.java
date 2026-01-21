package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.Product;
import com.sba302.electroshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<List<Product>> getAll() {
        return ApiResponse.success(productService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> getById(@PathVariable Integer id) {
        return productService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Product not found"));
    }

    @PostMapping
    public ApiResponse<Product> create(@RequestBody Product product) {
        return ApiResponse.success(productService.save(product));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        productService.deleteById(id);
        return ApiResponse.success(null);
    }
}
