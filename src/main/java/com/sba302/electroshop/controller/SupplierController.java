package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.Supplier;
import com.sba302.electroshop.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ApiResponse<List<Supplier>> getAll() {
        return ApiResponse.success(supplierService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Supplier> getById(@PathVariable Integer id) {
        return supplierService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Supplier not found"));
    }

    @PostMapping
    public ApiResponse<Supplier> create(@RequestBody Supplier supplier) {
        return ApiResponse.success(supplierService.save(supplier));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        supplierService.deleteById(id);
        return ApiResponse.success(null);
    }
}
