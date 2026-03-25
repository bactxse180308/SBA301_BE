package com.sba302.electroshop.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOrderDetailResponse {
    private Integer bulkOrderDetailId;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPriceSnapshot;
    private BigDecimal discountSnapshot;
    private BigDecimal appliedTierPrice;
    private BigDecimal customizationFeeConfirmed;
    private BigDecimal customizationFeePending;
    private BigDecimal lineTotal;

    private String productImage;
    private BigDecimal basePrice;
    private String tierLabel;
    private Integer branchId;
    private String branchName;

    private List<OrderCustomizationResponse> customizations;
}

