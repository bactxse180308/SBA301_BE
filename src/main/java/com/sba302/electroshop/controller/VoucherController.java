package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.Voucher;
import com.sba302.electroshop.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping
    public ApiResponse<List<Voucher>> getAll() {
        return ApiResponse.success(voucherService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Voucher> getById(@PathVariable Integer id) {
        return voucherService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Voucher not found"));
    }

    @PostMapping
    public ApiResponse<Voucher> create(@RequestBody Voucher voucher) {
        return ApiResponse.success(voucherService.save(voucher));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        voucherService.deleteById(id);
        return ApiResponse.success(null);
    }
}
