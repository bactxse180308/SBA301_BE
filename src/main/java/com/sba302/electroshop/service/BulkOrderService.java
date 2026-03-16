package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateBulkOrderRequest;
import com.sba302.electroshop.dto.request.CreateCustomizationRequest;
import com.sba302.electroshop.dto.response.BulkOrderResponse;
import com.sba302.electroshop.enums.BulkOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface BulkOrderService {

    BulkOrderResponse getById(Integer id);

    Page<BulkOrderResponse> search(Integer userId, Integer companyId, BulkOrderStatus status,
                                   LocalDateTime createdAtFrom, LocalDateTime createdAtTo,
                                   Pageable pageable);

    BulkOrderResponse create(Integer userId, CreateBulkOrderRequest request);

    BulkOrderResponse updateStatus(Integer id, BulkOrderStatus status, String note);

    BulkOrderResponse addCustomization(Integer bulkOrderDetailId, CreateCustomizationRequest request);

    BulkOrderResponse getPriceBreakdown(Integer bulkOrderId);

    BulkOrderResponse reviewCustomization(Integer customizationId, String status, BigDecimal extraFee, String feeType);

    BulkOrderResponse updateShippingFee(Integer id, BigDecimal shippingFee);

    BulkOrderResponse cancel(Integer id, String reason);
}
