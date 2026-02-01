package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.CreateSupplierRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.SupplierResponse;
import com.sba302.electroshop.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<SupplierResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(supplierService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<Page<SupplierResponse>> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(supplierService.search(keyword, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SupplierResponse> create(@Valid @RequestBody CreateSupplierRequest request) {
        return ApiResponse.success(supplierService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<SupplierResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody CreateSupplierRequest request) {
        return ApiResponse.success(supplierService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        supplierService.delete(id);
        return ApiResponse.success(null);
    }
}
