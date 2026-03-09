package com.sba302.electroshop.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StockItemResponse {
    private Integer productId;
    private String productName;
    private Integer quantity;
    private Integer branchId;
    private String branchName;
}
