package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.config.VNPayProperties;
import com.sba302.electroshop.config.VNPayUtil;
import com.sba302.electroshop.dto.response.PaymentTransactionResponse;
import com.sba302.electroshop.dto.response.VNPayPaymentUrlResponse;
import com.sba302.electroshop.entity.Order;
import com.sba302.electroshop.entity.PaymentTransaction;
import com.sba302.electroshop.enums.OrderStatus;
import com.sba302.electroshop.enums.PaymentStatus;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.repository.OrderRepository;
import com.sba302.electroshop.repository.PaymentTransactionRepository;
import com.sba302.electroshop.service.VNPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VNPayServiceImpl implements VNPayService {

    private final VNPayProperties vnPayProperties;
    private final OrderRepository orderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    @Transactional
    public VNPayPaymentUrlResponse createPaymentUrl(Integer orderId, String ipAddr) {
        log.info("Creating VNPay payment URL for orderId={}, ipAddr={}", orderId, ipAddr);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order total amount is invalid: " + order.getTotalAmount());
        }

        String txnRef = VNPayUtil.generateTxnRef(orderId);
        // VNPay amount = totalAmount * 100 (đơn vị VND, không có phần thập phân)
        long vnpAmount = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue();
        String createDate = VNPayUtil.formatDate(LocalDateTime.now());
        String expireDate = VNPayUtil.formatDate(LocalDateTime.now().plusMinutes(15));

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", vnPayProperties.getVersion());
        params.put("vnp_Command", vnPayProperties.getCommand());
        params.put("vnp_TmnCode", vnPayProperties.getTmnCode());
        params.put("vnp_Amount", String.valueOf(vnpAmount));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);
        params.put("vnp_OrderType", vnPayProperties.getOrderType());
        params.put("vnp_Locale", vnPayProperties.getLocale());
        params.put("vnp_ReturnUrl", vnPayProperties.getReturnUrl());
        params.put("vnp_IpAddr", ipAddr);
        params.put("vnp_CreateDate", createDate);
        params.put("vnp_ExpireDate", expireDate);

        // Tạo secure hash
        String hashData = VNPayUtil.buildHashData(params);
        String secureHash = VNPayUtil.hmacSHA512(vnPayProperties.getHashSecret(), hashData);
        params.put("vnp_SecureHash", secureHash);

        String paymentUrl = vnPayProperties.getPaymentUrl() + "?" + VNPayUtil.buildQueryString(params);

        // Lưu transaction với trạng thái PENDING
        PaymentTransaction transaction = PaymentTransaction.builder()
                .txnRef(txnRef)
                .order(order)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .secureHash(secureHash)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        paymentTransactionRepository.save(transaction);

        log.info("VNPay payment URL created. txnRef={}", txnRef);
        return VNPayPaymentUrlResponse.builder()
                .paymentUrl(paymentUrl)
                .txnRef(txnRef)
                .orderId(orderId)
                .build();
    }

    @Override
    @Transactional
    public Map<String, String> processIpn(Map<String, String> params) {
        log.info("Processing VNPay IPN: txnRef={}, responseCode={}",
                params.get("vnp_TxnRef"), params.get("vnp_ResponseCode"));

        Map<String, String> result = new HashMap<>();

        // 1. Verify secure hash
        String receivedHash = params.get("vnp_SecureHash");
        if (!verifySecureHash(params, receivedHash)) {
            log.warn("VNPay IPN: Invalid signature for txnRef={}", params.get("vnp_TxnRef"));
            result.put("RspCode", "97");
            result.put("Message", "Invalid signature");
            return result;
        }

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        String transactionNo = params.get("vnp_TransactionNo");
        String bankCode = params.get("vnp_BankCode");
        String bankTranNo = params.get("vnp_BankTranNo");
        String cardType = params.get("vnp_CardType");
        String payDate = params.get("vnp_PayDate");
        String vnpAmount = params.get("vnp_Amount");

        // 2. Tìm transaction
        PaymentTransaction transaction = paymentTransactionRepository.findByTxnRef(txnRef)
                .orElse(null);

        if (transaction == null) {
            log.warn("VNPay IPN: Transaction not found for txnRef={}", txnRef);
            result.put("RspCode", "01");
            result.put("Message", "Order not found");
            return result;
        }

        // 3. Kiểm tra đã xử lý chưa (idempotency)
        if (transaction.getStatus() != PaymentStatus.PENDING) {
            log.warn("VNPay IPN: Transaction already processed. txnRef={}, status={}",
                    txnRef, transaction.getStatus());
            result.put("RspCode", "02");
            result.put("Message", "Transaction already confirmed");
            return result;
        }

        // 4. Kiểm tra số tiền
        long expectedAmount = transaction.getAmount().multiply(BigDecimal.valueOf(100)).longValue();
        if (vnpAmount != null && !String.valueOf(expectedAmount).equals(vnpAmount)) {
            log.warn("VNPay IPN: Amount mismatch. txnRef={}, expected={}, received={}",
                    txnRef, expectedAmount, vnpAmount);
            result.put("RspCode", "04");
            result.put("Message", "Invalid amount");
            return result;
        }

        // 5. Cập nhật transaction
        transaction.setResponseCode(responseCode);
        transaction.setTransactionStatus(transactionStatus);
        transaction.setTransactionNo(transactionNo);
        transaction.setBankCode(bankCode);
        transaction.setBankTranNo(bankTranNo);
        transaction.setCardType(cardType);
        transaction.setPayDate(payDate);
        transaction.setUpdatedAt(LocalDateTime.now());

        boolean isSuccess = "00".equals(responseCode) && "00".equals(transactionStatus);
        if (isSuccess) {
            transaction.setStatus(PaymentStatus.SUCCESS);
            // Cập nhật order status
            Order order = transaction.getOrder();
            order.setOrderStatus(OrderStatus.CONFIRMED);
            order.setPaymentMethod("VNPAY");
            order.setPaymentStatus(PaymentStatus.SUCCESS);
            orderRepository.save(order);
            log.info("VNPay IPN: Payment success. txnRef={}, orderId={}", txnRef, order.getOrderId());
        } else {
            transaction.setStatus(PaymentStatus.FAILED);
            Order order = transaction.getOrder();
            order.setOrderStatus(OrderStatus.CANCELLED);
            order.setPaymentStatus(PaymentStatus.FAILED);
            orderRepository.save(order);
            log.info("VNPay IPN: Payment failed. txnRef={}, responseCode={}", txnRef, responseCode);
        }

        paymentTransactionRepository.save(transaction);

        result.put("RspCode", "00");
        result.put("Message", "Confirm Success");
        return result;
    }

    @Override
    @Transactional
    public PaymentTransactionResponse processReturn(Map<String, String> params) {
        log.info("Processing VNPay Return: txnRef={}, responseCode={}",
                params.get("vnp_TxnRef"), params.get("vnp_ResponseCode"));

        String receivedHash = params.get("vnp_SecureHash");
        boolean validHash = verifySecureHash(params, receivedHash);

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");

        PaymentTransaction transaction = paymentTransactionRepository.findByTxnRef(txnRef)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + txnRef));

        // Return URL chỉ cập nhật nếu transaction vẫn đang PENDING (IPN chưa xử lý)
        if (validHash && transaction.getStatus() == PaymentStatus.PENDING) {
            String transactionStatus = params.get("vnp_TransactionStatus");
            transaction.setResponseCode(responseCode);
            transaction.setTransactionStatus(transactionStatus);
            transaction.setTransactionNo(params.get("vnp_TransactionNo"));
            transaction.setBankCode(params.get("vnp_BankCode"));
            transaction.setBankTranNo(params.get("vnp_BankTranNo"));
            transaction.setCardType(params.get("vnp_CardType"));
            transaction.setPayDate(params.get("vnp_PayDate"));
            transaction.setUpdatedAt(LocalDateTime.now());

            boolean isSuccess = "00".equals(responseCode) && "00".equals(transactionStatus);
            if (isSuccess) {
                transaction.setStatus(PaymentStatus.SUCCESS);
                Order order = transaction.getOrder();
                order.setOrderStatus(OrderStatus.CONFIRMED);
                order.setPaymentMethod("VNPAY");
                order.setPaymentStatus(PaymentStatus.SUCCESS);
                orderRepository.save(order);
            } else {
                transaction.setStatus(PaymentStatus.FAILED);
                Order order = transaction.getOrder();
                order.setPaymentStatus(PaymentStatus.FAILED);
                orderRepository.save(order);
            }
            paymentTransactionRepository.save(transaction);
        }

        return toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionResponse> getTransactionsByOrderId(Integer orderId) {
        return paymentTransactionRepository.findByOrder_OrderId(orderId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ======================== PRIVATE HELPERS ========================

    /**
     * Verify VNPay secure hash: loại bỏ vnp_SecureHash khỏi params rồi tính lại
     */
    private boolean verifySecureHash(Map<String, String> params, String receivedHash) {
        if (receivedHash == null || receivedHash.isEmpty()) return false;

        Map<String, String> checkParams = new HashMap<>(params);
        checkParams.remove("vnp_SecureHash");
        checkParams.remove("vnp_SecureHashType");

        String hashData = VNPayUtil.buildHashData(checkParams);
        String computedHash = VNPayUtil.hmacSHA512(vnPayProperties.getHashSecret(), hashData);

        return computedHash.equalsIgnoreCase(receivedHash);
    }

    private PaymentTransactionResponse toResponse(PaymentTransaction t) {
        return PaymentTransactionResponse.builder()
                .id(t.getId())
                .txnRef(t.getTxnRef())
                .orderId(t.getOrder() != null ? t.getOrder().getOrderId() : null)
                .amount(t.getAmount())
                .bankCode(t.getBankCode())
                .bankTranNo(t.getBankTranNo())
                .cardType(t.getCardType())
                .responseCode(t.getResponseCode())
                .transactionNo(t.getTransactionNo())
                .transactionStatus(t.getTransactionStatus())
                .payDate(t.getPayDate())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}

