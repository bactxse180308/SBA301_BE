package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.CreatePriceTierRequest;
import com.sba302.electroshop.dto.request.UpdatePriceTierRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.BulkPriceTierResponse;
import com.sba302.electroshop.service.PriceTierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/price-tiers")
@RequiredArgsConstructor
public class PriceTierController {

    private final PriceTierService priceTierService;

    @GetMapping
    public ApiResponse<List<BulkPriceTierResponse>> getByProductId(
            @RequestParam Integer productId) {
        return ApiResponse.success(priceTierService.getByProductId(productId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BulkPriceTierResponse> create(
            @Valid @RequestBody CreatePriceTierRequest request) {
        return ApiResponse.success(priceTierService.create(request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<BulkPriceTierResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdatePriceTierRequest request) {
        return ApiResponse.success(priceTierService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        priceTierService.delete(id);
    }
}
