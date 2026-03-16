package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.*;
import com.sba302.electroshop.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/kpis")
    public ApiResponse<DashboardKpiResponse> getKpis() {
        return ApiResponse.success(adminDashboardService.getKpis());
    }

    @GetMapping("/revenue-trend")
    public ApiResponse<List<RevenueTrendResponse>> getRevenueTrend(
            @RequestParam(value = "days", required = false, defaultValue = "30") Integer days) {
        return ApiResponse.success(adminDashboardService.getRevenueTrend(days));
    }

    @GetMapping("/order-status-stats")
    public ApiResponse<List<OrderStatusStatResponse>> getOrderStatusStats(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.success(adminDashboardService.getOrderStatusStats(startDate, endDate));
    }

    @GetMapping("/top-products")
    public ApiResponse<List<TopProductResponse>> getTopProducts(
            @RequestParam(value = "limit", required = false, defaultValue = "8") Integer limit) {
        return ApiResponse.success(adminDashboardService.getTopProducts(limit));
    }

    @GetMapping("/customer-growth")
    public ApiResponse<List<CustomerGrowthResponse>> getCustomerGrowth(
            @RequestParam(value = "months", required = false, defaultValue = "6") Integer months) {
        return ApiResponse.success(adminDashboardService.getCustomerGrowth(months));
    }

    @GetMapping("/orders/recent")
    public ApiResponse<List<RecentOrderResponse>> getRecentOrders(
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit) {
        return ApiResponse.success(adminDashboardService.getRecentOrders(limit));
    }

    @GetMapping("/bulk-order-stats")
    public ApiResponse<BulkOrderStatsResponse> getBulkOrderStats() {
        return ApiResponse.success(adminDashboardService.getBulkOrderStats());
    }
}
 