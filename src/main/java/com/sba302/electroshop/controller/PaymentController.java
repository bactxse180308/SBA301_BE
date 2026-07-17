package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.PaymentTransactionResponse;
import com.sba302.electroshop.dto.response.VNPayPaymentUrlResponse;
import com.sba302.electroshop.enums.PaymentType;
import com.sba302.electroshop.service.VNPayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<VNPayPaymentUrlResponse> createPayment(
            @RequestParam Integer orderId,
            @RequestParam(defaultValue = "NORMAL") PaymentType type,
            HttpServletRequest request) {
        String ipAddr = getClientIp(request);
        VNPayPaymentUrlResponse response = vnPayService.createPaymentUrl(orderId, ipAddr, type);
        return ApiResponse.success(response);
    }

    @PostMapping("/vnpay/create/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<VNPayPaymentUrlResponse> createPaymentByOrderId(
            @PathVariable Integer orderId,
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
    public ResponseEntity<String> vnpayReturn(@RequestParam Map<String, String> params) {
        log.info("===== RETURN ENDPOINT HIT =====");
        log.info("vnp_ResponseCode: {}", params.get("vnp_ResponseCode"));
        log.info("vnp_TxnRef: {}", params.get("vnp_TxnRef"));
        log.info("vnp_TransactionStatus: {}", params.get("vnp_TransactionStatus"));
        log.info("vnp_Amount: {}", params.get("vnp_Amount"));
        log.info("vnp_BankCode: {}", params.get("vnp_BankCode"));
        log.info("vnp_OrderInfo: {}", params.get("vnp_OrderInfo"));
        log.info("vnp_PayDate: {}", params.get("vnp_PayDate"));
        log.info("vnp_TransactionNo: {}", params.get("vnp_TransactionNo"));
        log.info("vnp_SecureHash: {}", params.get("vnp_SecureHash"));
        log.info("Total params received: {}", params.size());
        log.info("================================");
        
        // Execute business logic (verifying hash, updating payment status, etc.)
        try {
            vnPayService.processReturn(params);
        } catch (Exception e) {
            log.error("Error processing VNPay return", e);
        }

        // Deep link construction
        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");

        String orderId = null;
        boolean parseSuccess = false;
        if (txnRef != null && !txnRef.trim().isEmpty()) {
            if (txnRef.contains("_")) {
                try {
                    orderId = txnRef.split("_")[0];
                    parseSuccess = true;
                } catch (Exception e) {
                    log.error("Error parsing orderId from txnRef: {}", txnRef, e);
                }
            } else {
                orderId = txnRef;
                parseSuccess = true;
            }
        }

        String deepLink;
        if (parseSuccess && orderId != null && !orderId.trim().isEmpty()) {
            boolean isSuccess = "00".equals(responseCode) && ("00".equals(transactionStatus) || transactionStatus == null);
            String status = isSuccess ? "SUCCESS" : "FAILED";
            deepLink = String.format("electroshop://payment-result?orderId=%s&status=%s&txnRef=%s", orderId, status, txnRef != null ? txnRef : "");
        } else {
            deepLink = "electroshop://payment-result?status=UNKNOWN";
        }

        log.info("Redirecting user to deep link: {}", deepLink);

        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Thanh toán ElectroShop</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif;\n" +
                "            display: flex;\n" +
                "            flex-direction: column;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            height: 100vh;\n" +
                "            margin: 0;\n" +
                "            background-color: #f8fafc;\n" +
                "            color: #1e293b;\n" +
                "        }\n" +
                "        .container {\n" +
                "            text-align: center;\n" +
                "            padding: 24px;\n" +
                "            background: #ffffff;\n" +
                "            border-radius: 16px;\n" +
                "            box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);\n" +
                "            max-width: 400px;\n" +
                "            width: 90%;\n" +
                "        }\n" +
                "        .spinner {\n" +
                "            border: 4px solid #f1f5f9;\n" +
                "            border-top: 4px solid #2563eb;\n" +
                "            border-radius: 50%;\n" +
                "            width: 40px;\n" +
                "            height: 40px;\n" +
                "            animation: spin 1s linear infinite;\n" +
                "            margin: 0 auto 20px;\n" +
                "        }\n" +
                "        @keyframes spin {\n" +
                "            0% { transform: rotate(0deg); }\n" +
                "            100% { transform: rotate(360deg); }\n" +
                "        }\n" +
                "        h2 {\n" +
                "            font-size: 1.25rem;\n" +
                "            margin-bottom: 8px;\n" +
                "            color: #0f172a;\n" +
                "        }\n" +
                "        p {\n" +
                "            font-size: 0.875rem;\n" +
                "            color: #64748b;\n" +
                "            margin-bottom: 24px;\n" +
                "        }\n" +
                "        .btn {\n" +
                "            display: inline-block;\n" +
                "            padding: 12px 24px;\n" +
                "            background-color: #2563eb;\n" +
                "            color: #ffffff;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 8px;\n" +
                "            font-weight: 600;\n" +
                "            font-size: 0.875rem;\n" +
                "            transition: background-color 0.2s;\n" +
                "        }\n" +
                "        .btn:hover {\n" +
                "            background-color: #1d4ed8;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"spinner\"></div>\n" +
                "        <h2>Đang chuyển về ứng dụng ElectroShop...</h2>\n" +
                "        <p>Vui lòng đợi trong giây lát. Nếu trình duyệt không tự chuyển hướng, bấm nút bên dưới.</p>\n" +
                "        <a class=\"btn\" href=\"" + deepLink + "\">Quay lại ứng dụng</a>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        setTimeout(function() {\n" +
                "            window.location.href = \"" + deepLink + "\";\n" +
                "        }, 500);\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html);
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
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        if (ip != null && ip.contains(":")) {
            log.warn("VNPay does not accept IPv6 vnp_IpAddr values. Falling back to 127.0.0.1. originalIp={}", ip);
            return "127.0.0.1";
        }
        return ip;
    }
}

