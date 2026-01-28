package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.CreateVoucherRequest;
import com.sba302.electroshop.dto.request.UpdateVoucherRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.VoucherResponse;
import com.sba302.electroshop.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping("/{id}")
    public ApiResponse<VoucherResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(voucherService.getById(id));
    }

    @GetMapping("/code/{code}")
    public ApiResponse<VoucherResponse> getByCode(@PathVariable String code) {
        return ApiResponse.success(voucherService.getByCode(code));
    }

    @GetMapping
    public ApiResponse<Page<VoucherResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean validOnly,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(voucherService.search(keyword, validOnly, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<VoucherResponse> create(@Valid @RequestBody CreateVoucherRequest request) {
        return ApiResponse.success(voucherService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<VoucherResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateVoucherRequest request) {
        return ApiResponse.success(voucherService.update(id, request));
    }

    @PostMapping("/{voucherId}/assign/{userId}")
    public ApiResponse<Void> assignToUser(
            @PathVariable Integer voucherId,
            @PathVariable Integer userId) {
        voucherService.assignToUser(voucherId, userId);
        return ApiResponse.success(null);
    }

    @GetMapping("/validate")
    public ApiResponse<Boolean> validateVoucher(
            @RequestParam String code,
            @RequestParam Integer userId) {
        return ApiResponse.success(voucherService.validateVoucher(code, userId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        voucherService.delete(id);
        return ApiResponse.success(null);
    }
}
