package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.CreateWarrantyRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.WarrantyResponse;
import com.sba302.electroshop.service.WarrantyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warranties")
@RequiredArgsConstructor
public class WarrantyController {

    private final WarrantyService warrantyService;

    @GetMapping("/{id}")
    public ApiResponse<WarrantyResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(warrantyService.getById(id));
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<Page<WarrantyResponse>> getByProduct(
            @PathVariable Integer productId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(warrantyService.getByProduct(productId, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WarrantyResponse> create(@Valid @RequestBody CreateWarrantyRequest request) {
        return ApiResponse.success(warrantyService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<WarrantyResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody CreateWarrantyRequest request) {
        return ApiResponse.success(warrantyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        warrantyService.delete(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/validate")
    public ApiResponse<Boolean> isWarrantyValid(@PathVariable Integer id) {
        return ApiResponse.success(warrantyService.isWarrantyValid(id));
    }
}
