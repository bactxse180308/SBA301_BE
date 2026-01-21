package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.Warranty;
import com.sba302.electroshop.service.WarrantyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/warranties")
@RequiredArgsConstructor
public class WarrantyController {

    private final WarrantyService warrantyService;

    @GetMapping
    public ApiResponse<List<Warranty>> getAll() {
        return ApiResponse.success(warrantyService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Warranty> getById(@PathVariable Integer id) {
        return warrantyService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Warranty not found"));
    }

    @PostMapping
    public ApiResponse<Warranty> create(@RequestBody Warranty warranty) {
        return ApiResponse.success(warrantyService.save(warranty));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        warrantyService.deleteById(id);
        return ApiResponse.success(null);
    }
}
