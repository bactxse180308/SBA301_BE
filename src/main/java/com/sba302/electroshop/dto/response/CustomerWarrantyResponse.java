package com.sba302.electroshop.dto.response;

import com.sba302.electroshop.enums.CustomerWarrantyStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CustomerWarrantyResponse {

    private Integer id;

    private Integer productId;
    private String productName;
    private String productImage;

    /** orderId nếu bảo hành từ đơn thường, null nếu từ bulk */
    private Integer orderId;

    /** bulkOrderId nếu bảo hành từ đơn B2B, null nếu từ đơn thường */
    private Integer bulkOrderId;

    /** "ORDER" hoặc "BULK_ORDER" để FE phân biệt nguồn gốc */
    private String orderType;

    private Integer quantity;
    private Integer warrantyMonths;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private CustomerWarrantyStatus status;

    /** Số ngày còn lại đến khi hết hạn bảo hành (âm nếu đã hết hạn) */
    private Long daysRemaining;

    /** true nếu đã hết hạn (endDate < now) */
    private Boolean isExpired;

    private String notes;
}
