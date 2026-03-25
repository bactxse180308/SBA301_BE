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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/bulk-orders")
@RequiredArgsConstructor
public class BulkOrderController {

    private final BulkOrderService bulkOrderService;

    @PreAuthorize("hasRole('ADMIN') or @bulkOrderSecurity.isOwner(#id)")
    @GetMapping("/{id}")
    public ApiResponse<BulkOrderResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(bulkOrderService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or (#userId != null and authentication.principal.toString() == #userId.toString())")
    public ApiResponse<Page<BulkOrderResponse>> search(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer companyId,
            @RequestParam(required = false) BulkOrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtTo,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(bulkOrderService.search(userId, companyId, status, createdAtFrom, createdAtTo, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("authentication.principal.toString() == #userId.toString()")
    public ApiResponse<BulkOrderResponse> create(
            @RequestParam Integer userId,
            @Valid @RequestBody CreateBulkOrderRequest request) {
        return ApiResponse.success(bulkOrderService.create(userId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ApiResponse<BulkOrderResponse> updateStatus(
            @PathVariable Integer id,
            @RequestParam BulkOrderStatus status,
            @RequestParam(required = false) String note) {
        return ApiResponse.success(bulkOrderService.updateStatus(id, status, note));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/shipping-fee")
    public ApiResponse<BulkOrderResponse> updateShippingFee(
            @PathVariable Integer id,
            @RequestParam BigDecimal shippingFee) {
        return ApiResponse.success(bulkOrderService.updateShippingFee(id, shippingFee));
    }

    @PostMapping("/details/{detailId}/customization")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<BulkOrderResponse> addCustomization(
            @PathVariable Integer detailId,
            @Valid @RequestBody CreateCustomizationRequest request) {
        return ApiResponse.success(bulkOrderService.addCustomization(detailId, request));
    }

    @PreAuthorize("hasRole('ADMIN') or @bulkOrderSecurity.isOwner(#id)")
    @GetMapping("/{id}/price-breakdown")
    public ApiResponse<BulkOrderResponse> getPriceBreakdown(@PathVariable Integer id) {
        return ApiResponse.success(bulkOrderService.getPriceBreakdown(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/customizations/{customizationId}/review")
    public ApiResponse<BulkOrderResponse> reviewCustomization(
            @PathVariable Integer customizationId,
            @RequestParam String status,
            @RequestParam BigDecimal extraFee,
            @RequestParam(required = false) String feeType) {
        return ApiResponse.success(bulkOrderService.reviewCustomization(customizationId, status, extraFee, feeType));
    }

    @PreAuthorize("hasRole('ADMIN') or @bulkOrderSecurity.isOwner(#id)")
    @PostMapping("/{id}/cancel")
    public ApiResponse<BulkOrderResponse> cancel(
            @PathVariable Integer id,
            @RequestParam(required = false) String reason) {
        return ApiResponse.success(bulkOrderService.cancel(id, reason));
    }
}
