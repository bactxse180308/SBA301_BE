package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.CreateStoreBranchRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.StoreBranchResponse;
import com.sba302.electroshop.service.StoreBranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class StoreBranchController {

    private final StoreBranchService storeBranchService;

    @GetMapping("/{id}")
    public ApiResponse<StoreBranchResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(storeBranchService.getById(id));
    }

    @GetMapping("/all")
    public ApiResponse<List<StoreBranchResponse>> getAll() {
        return ApiResponse.success(storeBranchService.getAll());
    }

    @GetMapping
    public ApiResponse<Page<StoreBranchResponse>> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(storeBranchService.search(keyword, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<StoreBranchResponse> create(@Valid @RequestBody CreateStoreBranchRequest request) {
        return ApiResponse.success(storeBranchService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StoreBranchResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody CreateStoreBranchRequest request) {
        return ApiResponse.success(storeBranchService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        storeBranchService.delete(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{branchId}/products/{productId}/stock")
    public ApiResponse<Integer> getStockQuantity(
            @PathVariable Integer branchId,
            @PathVariable Integer productId) {
        return ApiResponse.success(storeBranchService.getStockQuantity(branchId, productId));
    }

    @PatchMapping("/{branchId}/products/{productId}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateStock(
            @PathVariable Integer branchId,
            @PathVariable Integer productId,
            @RequestParam Integer quantity) {
        storeBranchService.updateStock(branchId, productId, quantity);
        return ApiResponse.success(null);
    }
}
