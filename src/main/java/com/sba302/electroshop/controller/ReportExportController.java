package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.service.ReportExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/api/v1/admin/reports/export")
@RequiredArgsConstructor
@Slf4j
public class ReportExportController {

    private final ReportExportService reportExportService;

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> exportRevenue(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate) {
        
        if (startDate == null) startDate = java.time.LocalDate.now().withDayOfMonth(1);
        if (endDate == null) endDate = java.time.LocalDate.now();
        
        log.info("REST request to export revenue report");
        byte[] data = reportExportService.exportRevenueReport(startDate, endDate);
        return ApiResponse.success(Base64.getEncoder().encodeToString(data));
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> exportInventory() {
        log.info("REST request to export inventory report");
        byte[] data = reportExportService.exportInventoryReport();
        return ApiResponse.success(Base64.getEncoder().encodeToString(data));
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> exportTopProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("REST request to export top products report");
        byte[] data = reportExportService.exportTopProductsReport(limit);
        return ApiResponse.success(Base64.getEncoder().encodeToString(data));
    }
}
