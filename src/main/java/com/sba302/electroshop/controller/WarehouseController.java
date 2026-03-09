package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.StockExportRequest;
import com.sba302.electroshop.dto.request.StockImportRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.StockCheckResult;
import com.sba302.electroshop.dto.response.StockItemResponse;
import com.sba302.electroshop.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/inventory")
    public ApiResponse<Page<StockItemResponse>> getInventory(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer branchId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(warehouseService.getInventory(q, branchId, pageable));
    }

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> importStock(@Valid @RequestBody StockImportRequest request) {
        warehouseService.importStock(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/export")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> exportStock(@Valid @RequestBody StockExportRequest request) {
        warehouseService.exportStock(request);
        return ApiResponse.success(null);
    }

    @GetMapping("/stock-check")
    public ApiResponse<StockCheckResult> checkStock(
            @RequestParam Integer branchId,
            @RequestParam Integer productId) {
        return ApiResponse.success(warehouseService.checkStock(branchId, productId));
    }
}
