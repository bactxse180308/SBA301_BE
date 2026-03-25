package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.CreateProductRequest;
import com.sba302.electroshop.dto.request.UpdateProductRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.ProductResponse;
import com.sba302.electroshop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(productService.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<ProductResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer brandId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(productService.search(keyword, categoryId, brandId, pageable));
    }

    @GetMapping("/search")
    public ApiResponse<Page<ProductResponse>> searchEndpoint(
            @RequestParam(value = "q", required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(productService.search(q, null, null, pageable));
    }

    @GetMapping("/company/{id}")
    public ApiResponse<com.sba302.electroshop.dto.response.CompanyProductResponse> getCompanyById(@PathVariable Integer id) {
        return ApiResponse.success(productService.getCompanyProductById(id));
    }

    @GetMapping("/company/search")
    public ApiResponse<Page<com.sba302.electroshop.dto.response.CompanyProductResponse>> searchForCompany(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer brandId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(productService.searchForCompany(keyword, categoryId, brandId, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        return ApiResponse.success(productService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ApiResponse.success(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        productService.delete(id);
        return ApiResponse.success(null);
    }

}
