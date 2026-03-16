package com.sba302.electroshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOrderStatsResponse {
    private Long pendingReview;
    private Long awaitingPayment;
    private Long processing;
    private BigDecimal revenueThisMonth;
    private Long newOrdersToday;
}
