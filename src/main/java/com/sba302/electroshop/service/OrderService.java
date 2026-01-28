package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateOrderRequest;
import com.sba302.electroshop.dto.response.OrderResponse;
import com.sba302.electroshop.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    // Query (combined with optional filters)
    OrderResponse getById(Integer id);

    Page<OrderResponse> search(Integer userId, OrderStatus status, Pageable pageable);

    // Business operations
    OrderResponse placeOrder(Integer userId, CreateOrderRequest request);

    OrderResponse updateStatus(Integer orderId, OrderStatus newStatus);

    void cancelOrder(Integer orderId);

    OrderResponse applyVoucher(Integer orderId, String voucherCode);
}
