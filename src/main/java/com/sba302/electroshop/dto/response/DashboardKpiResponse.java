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
public class DashboardKpiResponse {
    private KpiItem<BigDecimal> totalRevenue;
    private KpiItem<Integer> ordersToday;
    private KpiItem<Integer> activeProducts;
    private KpiItem<Integer> totalCustomers;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KpiItem<T> {
        private T value;
        private Double change;
    }
}
