package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.response.CustomerWarrantyResponse;
import com.sba302.electroshop.entity.BulkOrder;
import com.sba302.electroshop.entity.Order;
import com.sba302.electroshop.enums.CustomerWarrantyStatus;

import java.util.List;

public interface CustomerWarrantyService {

    /** User xem tất cả bảo hành của chính mình (sắp xếp theo ngày hết hạn) */
    List<CustomerWarrantyResponse> getMyWarranties(Integer userId);

    /** User xem bảo hành còn hiệu lực (status = ACTIVE và endDate >= now) */
    List<CustomerWarrantyResponse> getMyActiveWarranties(Integer userId);

    /** Tra cứu bảo hành theo đơn hàng thường */
    List<CustomerWarrantyResponse> getByOrderId(Integer orderId);

    /** Tra cứu bảo hành theo đơn hàng B2B */
    List<CustomerWarrantyResponse> getByBulkOrderId(Integer bulkOrderId);

    /** Admin xem bảo hành của một user bất kỳ */
    List<CustomerWarrantyResponse> getByUserId(Integer userId);

    /**
     * Tự động tạo CustomerWarranty cho từng OrderDetail.
     * Lấy warranty_months từ bảng WARRANTY theo product_id.
     * Bỏ qua nếu product không có Warranty hoặc đã tạo rồi.
     */
    void createFromOrder(Order order);

    /**
     * Tự động tạo CustomerWarranty cho từng BulkOrderDetail.
     * Bỏ qua nếu product không có Warranty hoặc đã tạo rồi.
     */
    void createFromBulkOrder(BulkOrder bulkOrder);

    /** Admin cập nhật notes và/hoặc status của một CustomerWarranty */
    CustomerWarrantyResponse update(Integer id, String notes, CustomerWarrantyStatus status);
}
