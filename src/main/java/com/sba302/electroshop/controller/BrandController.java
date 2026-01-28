package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.CreateBrandRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.BrandResponse;
import com.sba302.electroshop.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping("/{id}")
    public ApiResponse<BrandResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(brandService.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<BrandResponse>> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(brandService.search(keyword, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BrandResponse> create(@Valid @RequestBody CreateBrandRequest request) {
        return ApiResponse.success(brandService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<BrandResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody CreateBrandRequest request) {
        return ApiResponse.success(brandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        brandService.delete(id);
        return ApiResponse.success(null);
    }
}
