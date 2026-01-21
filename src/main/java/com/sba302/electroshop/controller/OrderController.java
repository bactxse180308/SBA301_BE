package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.Order;
import com.sba302.electroshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ApiResponse<List<Order>> getAll() {
        return ApiResponse.success(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Order> getById(@PathVariable Integer id) {
        return orderService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Order not found"));
    }

    @PostMapping
    public ApiResponse<Order> create(@RequestBody Order order) {
        return ApiResponse.success(orderService.save(order));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        orderService.deleteById(id);
        return ApiResponse.success(null);
    }
}
