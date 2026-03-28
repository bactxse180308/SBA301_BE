package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.service.BulkOrderExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/api/v1/bulk-orders/{bulkOrderId}/export")
@RequiredArgsConstructor
@Slf4j
public class BulkOrderExportController {

    private final BulkOrderExportService bulkOrderExportService;

    /**
     * Xuất đơn xác nhận đơn hàng (Sales Order Confirmation).
     * Chỉ ADMIN được phép xuất.
     * Thường dùng khi đơn ở trạng thái CONFIRMED.
     *
     * GET /api/v1/bulk-orders/{bulkOrderId}/export/order-confirmation
     */
    @GetMapping("/order-confirmation")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> exportOrderConfirmation(@PathVariable Integer bulkOrderId) {
        log.info("REST request to export Order Confirmation for bulkOrderId={}", bulkOrderId);
        byte[] data = bulkOrderExportService.exportOrderConfirmation(bulkOrderId);
        return ApiResponse.success(Base64.getEncoder().encodeToString(data));
    }

    /**
     * Xuất hóa đơn bán hàng (Invoice / VAT Invoice).
     * ADMIN hoặc chính Company sở hữu đơn đều được phép xuất.
     * Thường dùng khi đơn ở trạng thái SHIPPED hoặc COMPLETED.
     *
     * GET /api/v1/bulk-orders/{bulkOrderId}/export/invoice
     */
    @GetMapping("/invoice")
    @PreAuthorize("hasRole('ADMIN') or @bulkOrderSecurity.isOwner(#bulkOrderId)")
    public ApiResponse<String> exportInvoice(@PathVariable Integer bulkOrderId) {
        log.info("REST request to export Invoice for bulkOrderId={}", bulkOrderId);
        byte[] data = bulkOrderExportService.exportInvoice(bulkOrderId);
        return ApiResponse.success(Base64.getEncoder().encodeToString(data));
    }
}