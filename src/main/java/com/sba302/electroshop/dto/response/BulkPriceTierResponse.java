package com.sba302.electroshop.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkPriceTierResponse {
    private Integer bulkPriceTierId;
    private Integer minQty;
    private BigDecimal unitPrice;
}

