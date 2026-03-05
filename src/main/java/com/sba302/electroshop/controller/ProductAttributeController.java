package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.CreateProductAttributeRequest;
import com.sba302.electroshop.dto.request.UpdateProductAttributeRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.ProductAttributeResponse;
import com.sba302.electroshop.service.ProductAttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-attributes")
@RequiredArgsConstructor
public class ProductAttributeController {

    private final ProductAttributeService productAttributeService;

    @GetMapping("/{id}")
    public ApiResponse<ProductAttributeResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(productAttributeService.getById(id));
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<ProductAttributeResponse>> getByProduct(@PathVariable Integer productId) {
        return ApiResponse.success(productAttributeService.getByProduct(productId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductAttributeResponse> create(@Valid @RequestBody CreateProductAttributeRequest request) {
        return ApiResponse.success(productAttributeService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductAttributeResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProductAttributeRequest request) {
        return ApiResponse.success(productAttributeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        productAttributeService.delete(id);
        return ApiResponse.success(null);
    }
}
