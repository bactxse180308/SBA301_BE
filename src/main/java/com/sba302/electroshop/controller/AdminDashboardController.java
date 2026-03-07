package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.*;
import com.sba302.electroshop.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    // Helper method to wrap response matching the specification
    private <T> ResponseEntity<Map<String, Object>> successResponse(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "Success");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> getKpis() {
        DashboardKpiResponse data = adminDashboardService.getKpis();
        return successResponse(data);
    }

    @GetMapping("/revenue-trend")
    public ResponseEntity<Map<String, Object>> getRevenueTrend(
            @RequestParam(value = "days", required = false, defaultValue = "30") Integer days) {
        List<RevenueTrendResponse> data = adminDashboardService.getRevenueTrend(days);
        return successResponse(data);
    }

    @GetMapping("/order-status-stats")
    public ResponseEntity<Map<String, Object>> getOrderStatusStats(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<OrderStatusStatResponse> data = adminDashboardService.getOrderStatusStats(startDate, endDate);
        return successResponse(data);
    }

    @GetMapping("/top-products")
    public ResponseEntity<Map<String, Object>> getTopProducts(
            @RequestParam(value = "limit", required = false, defaultValue = "8") Integer limit) {
        List<TopProductResponse> data = adminDashboardService.getTopProducts(limit);
        return successResponse(data);
    }

    @GetMapping("/customer-growth")
    public ResponseEntity<Map<String, Object>> getCustomerGrowth(
            @RequestParam(value = "months", required = false, defaultValue = "6") Integer months) {
        List<CustomerGrowthResponse> data = adminDashboardService.getCustomerGrowth(months);
        return successResponse(data);
    }

    // Usually /orders/recent is better but we put it here or as a separate endpoint
    @GetMapping("/orders/recent")
    public ResponseEntity<Map<String, Object>> getRecentOrders(
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit) {
        List<RecentOrderResponse> data = adminDashboardService.getRecentOrders(limit);
        return successResponse(data);
    }
}
