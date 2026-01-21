package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.BulkOrder;
import com.sba302.electroshop.service.BulkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bulk-orders")
@RequiredArgsConstructor
public class BulkOrderController {

    private final BulkOrderService bulkOrderService;

    @GetMapping
    public ApiResponse<List<BulkOrder>> getAll() {
        return ApiResponse.success(bulkOrderService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<BulkOrder> getById(@PathVariable Integer id) {
        return bulkOrderService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "BulkOrder not found"));
    }

    @PostMapping
    public ApiResponse<BulkOrder> create(@RequestBody BulkOrder bulkOrder) {
        return ApiResponse.success(bulkOrderService.save(bulkOrder));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        bulkOrderService.deleteById(id);
        return ApiResponse.success(null);
    }
}
