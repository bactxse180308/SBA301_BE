package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.response.PaymentTransactionResponse;
import com.sba302.electroshop.dto.response.VNPayPaymentUrlResponse;
import com.sba302.electroshop.enums.PaymentType;

import java.util.List;
import java.util.Map;

public interface VNPayService {

    VNPayPaymentUrlResponse createPaymentUrl(Integer orderId, String ipAddr, PaymentType type);

    Map<String, String> processIpn(Map<String, String> params);

    PaymentTransactionResponse processReturn(Map<String, String> params);

    List<PaymentTransactionResponse> getTransactionsByOrderId(Integer orderId);
}

