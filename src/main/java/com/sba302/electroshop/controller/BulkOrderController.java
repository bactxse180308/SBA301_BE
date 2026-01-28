package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.CreateBulkOrderRequest;
import com.sba302.electroshop.dto.request.CreateCustomizationRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.BulkOrderResponse;
import com.sba302.electroshop.enums.BulkOrderStatus;
import com.sba302.electroshop.service.BulkOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bulk-orders")
@RequiredArgsConstructor
public class BulkOrderController {

    private final BulkOrderService bulkOrderService;

    @GetMapping("/{id}")
    public ApiResponse<BulkOrderResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(bulkOrderService.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<BulkOrderResponse>> search(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) BulkOrderStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(bulkOrderService.search(userId, status, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BulkOrderResponse> create(
            @RequestParam Integer userId,
            @Valid @RequestBody CreateBulkOrderRequest request) {
        return ApiResponse.success(bulkOrderService.create(userId, request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<BulkOrderResponse> updateStatus(
            @PathVariable Integer id,
            @RequestParam BulkOrderStatus status) {
        return ApiResponse.success(bulkOrderService.updateStatus(id, status));
    }

    @PostMapping("/details/{detailId}/customization")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BulkOrderResponse> addCustomization(
            @PathVariable Integer detailId,
            @Valid @RequestBody CreateCustomizationRequest request) {
        return ApiResponse.success(bulkOrderService.addCustomization(detailId, request));
    }
}
