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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or isAuthenticated()")
    public ApiResponse<VoucherResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(voucherService.getById(id));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<VoucherResponse> getByCode(@PathVariable String code) {
        return ApiResponse.success(voucherService.getByCode(code));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<Page<VoucherResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean validOnly,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(voucherService.search(keyword, validOnly, pageable));
    }

    @GetMapping("/my-vouchers")
    @PreAuthorize("authentication.principal.toString() == #userId.toString()")
    public ApiResponse<Page<VoucherResponse>> getMyVouchers(
            @RequestParam Integer userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(voucherService.getVouchersByUserId(userId, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<VoucherResponse> create(@Valid @RequestBody CreateVoucherRequest request) {
        return ApiResponse.success(voucherService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<VoucherResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateVoucherRequest request) {
        return ApiResponse.success(voucherService.update(id, request));
    }

    @PostMapping("/{voucherId}/assign/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<Void> assignToUser(
            @PathVariable Integer voucherId,
            @PathVariable Integer userId) {
        voucherService.assignToUser(voucherId, userId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{voucherId}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<Void> assignToUsers(
            @PathVariable Integer voucherId,
            @RequestBody java.util.List<Integer> userIds) {
        voucherService.assignToUsers(voucherId, userIds);
        return ApiResponse.success(null);
    }

    @GetMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Boolean> validateVoucher(
            @RequestParam String code,
            @RequestParam Integer userId) {
        return ApiResponse.success(voucherService.validateVoucher(code, userId));
    }

    @GetMapping("/validate-details")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<VoucherResponse> validateAndGetVoucher(
            @RequestParam String code,
            @RequestParam Integer userId,
            @RequestParam BigDecimal orderTotal) {
        voucherService.validateAndGetVoucher(code, userId, orderTotal);
        return ApiResponse.success(voucherService.getByCode(code));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        voucherService.delete(id);
        return ApiResponse.success(null);
    }
}
