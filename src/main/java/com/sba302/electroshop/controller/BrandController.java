package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.Brand;
import com.sba302.electroshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ApiResponse<List<Brand>> getAll() {
        return ApiResponse.success(brandService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Brand> getById(@PathVariable Integer id) {
        return brandService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Brand not found"));
    }

    @PostMapping
    public ApiResponse<Brand> create(@RequestBody Brand brand) {
        return ApiResponse.success(brandService.save(brand));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        brandService.deleteById(id);
        return ApiResponse.success(null);
    }
}
