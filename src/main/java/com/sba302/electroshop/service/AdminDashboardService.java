package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.response.*;
import java.time.LocalDate;
import java.util.List;

public interface AdminDashboardService {
    DashboardKpiResponse getKpis();
    
    List<RevenueTrendResponse> getRevenueTrend(Integer days);
    
    List<OrderStatusStatResponse> getOrderStatusStats(LocalDate startDate, LocalDate endDate);
    
    List<TopProductResponse> getTopProducts(Integer limit);
    
    List<CustomerGrowthResponse> getCustomerGrowth(Integer months);
    
    List<RecentOrderResponse> getRecentOrders(Integer limit);
}
