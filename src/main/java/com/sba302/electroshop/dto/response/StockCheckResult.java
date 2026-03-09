package com.sba302.electroshop.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StockCheckResult {
    private Integer productId;
    private Integer branchId;
    private Integer availableQuantity;
}
