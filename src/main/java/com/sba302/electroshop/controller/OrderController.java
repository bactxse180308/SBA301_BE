package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.CreateOrderRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.OrderResponse;
import com.sba302.electroshop.enums.OrderStatus;
import com.sba302.electroshop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOwner(#id)")
    public ApiResponse<OrderResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(orderService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or (#userId != null and authentication.principal.toString() == #userId.toString())")
    public ApiResponse<Page<OrderResponse>> search(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(orderService.search(userId, status, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("authentication.principal.toString() == #userId.toString()")
    public ApiResponse<OrderResponse> placeOrder(
            @RequestParam Integer userId,
            @Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.success(orderService.placeOrder(userId, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<OrderResponse> updateStatus(
            @PathVariable Integer id,
            @RequestParam OrderStatus status) {
        return ApiResponse.success(orderService.updateStatus(id, status));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOwner(#id)")
    public ApiResponse<Void> cancelOrder(
            @PathVariable Integer id,
            @RequestParam(required = false) String reason) {
        orderService.cancelOrder(id, reason);
        return ApiResponse.success(null);
    }
}
