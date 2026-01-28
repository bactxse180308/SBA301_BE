package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateBulkOrderRequest;
import com.sba302.electroshop.dto.request.CreateCustomizationRequest;
import com.sba302.electroshop.dto.response.BulkOrderResponse;
import com.sba302.electroshop.enums.BulkOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BulkOrderService {

    BulkOrderResponse getById(Integer id);

    Page<BulkOrderResponse> search(Integer userId, BulkOrderStatus status, Pageable pageable);

    BulkOrderResponse create(Integer userId, CreateBulkOrderRequest request);

    BulkOrderResponse updateStatus(Integer id, BulkOrderStatus status);

    BulkOrderResponse addCustomization(Integer bulkOrderDetailId, CreateCustomizationRequest request);
}
