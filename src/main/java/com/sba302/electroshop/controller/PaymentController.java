package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.PaymentTransactionResponse;
import com.sba302.electroshop.dto.response.VNPayPaymentUrlResponse;
import com.sba302.electroshop.enums.PaymentType;
import com.sba302.electroshop.service.VNPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment", description = "VNPay payment integration")
public class PaymentController {

    private final VNPayService vnPayService;

    @PostMapping("/vnpay/create")
    public ApiResponse<VNPayPaymentUrlResponse> createPayment(
            @RequestParam Integer orderId,
            @RequestParam(defaultValue = "NORMAL") PaymentType type,
            HttpServletRequest request) {
        String ipAddr = getClientIp(request);
        VNPayPaymentUrlResponse response = vnPayService.createPaymentUrl(orderId, ipAddr, type);
        return ApiResponse.success(response);
    }

    @GetMapping("/vnpay/ipn")
    public ResponseEntity<Map<String, String>> vnpayIpn(@RequestParam Map<String, String> params) {
        log.info("VNPay IPN received: {}", params);
        Map<String, String> result = vnPayService.processIpn(params);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/vnpay/return")
    public ApiResponse<PaymentTransactionResponse> vnpayReturn(@RequestParam Map<String, String> params) {
        log.info("VNPay Return received: txnRef={}, responseCode={}",
                params.get("vnp_TxnRef"), params.get("vnp_ResponseCode"));
        PaymentTransactionResponse response = vnPayService.processReturn(params);
        return ApiResponse.success(response);
    }

    @GetMapping("/orders/{orderId}")
    public ApiResponse<List<PaymentTransactionResponse>> getTransactionsByOrder(@PathVariable Integer orderId) {
        return ApiResponse.success(vnPayService.getTransactionsByOrderId(orderId));
    }


    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

