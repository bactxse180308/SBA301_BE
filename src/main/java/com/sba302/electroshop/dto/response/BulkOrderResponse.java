package com.sba302.electroshop.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOrderResponse {
    private Integer bulkOrderId;
    private Integer userId;
    private String userFullName;
    private Integer companyId;
    private String companyName;
    private LocalDateTime createdAt;
    private String status;
    private BigDecimal subtotalAfterTier;
    private String voucherCode;
    private String voucherType;
    private BigDecimal voucherDiscountAmount;
    private BigDecimal shippingFee;
    private Boolean shippingFeeWaived;
    private BigDecimal finalPrice;
    private String cancelReason;
    private Boolean discountApplied;
    private LocalDateTime updatedAt;
    private String shippingAddress;
    private String userEmail;
    private String userPhone;
    private String adminNote;

    // Breakdown fields
    private BigDecimal basePriceTotal;
    private BigDecimal tierDiscountTotal;
    private BigDecimal customizationFeeConfirmed;
    private BigDecimal customizationFeePending;

    // UI flags
    private Boolean hasPendingCustomization;

    private List<BulkOrderDetailResponse> details;
}
