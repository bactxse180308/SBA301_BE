package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.StockTransactionResponse;
import com.sba302.electroshop.enums.TransactionType;
import com.sba302.electroshop.service.StockTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stock-transactions")
@RequiredArgsConstructor
public class StockTransactionController {

    private final StockTransactionService stockTransactionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<StockTransactionResponse>> getTransactions(
            @RequestParam(required = false) Integer branchId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Integer orderId,
            @RequestParam(required = false) Integer bulkOrderId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<StockTransactionResponse> result = stockTransactionService.getTransactions(
                branchId, type, orderId, bulkOrderId, pageable);

        return ApiResponse.success(result);
    }
}
