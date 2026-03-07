package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.response.*;
import com.sba302.electroshop.entity.DailyStatistic;
import com.sba302.electroshop.repository.DailyStatisticRepository;
import com.sba302.electroshop.repository.OrderDetailRepository;
import com.sba302.electroshop.repository.OrderRepository;
import com.sba302.electroshop.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final DailyStatisticRepository dailyStatisticRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public DashboardKpiResponse getKpis() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate thirtyDaysAgo = today.minusDays(30);

        DailyStatistic statToday = dailyStatisticRepository.findByStatisticDate(today).orElse(new DailyStatistic());
        DailyStatistic statYesterday = dailyStatisticRepository.findByStatisticDate(yesterday).orElse(new DailyStatistic());
        DailyStatistic stat30DaysAgo = dailyStatisticRepository.findByStatisticDate(thirtyDaysAgo).orElse(new DailyStatistic());

        // Total Revenue (All time or we can just use today's for now, let's sum all from daily statistic)
        BigDecimal totalRevenue = dailyStatisticRepository.findAll().stream()
                .map(DailyStatistic::getTotalRevenue)
                .filter(r -> r != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Revenue Change (Today vs Yesterday for simplicity, or we can do Month over Month)
        BigDecimal revToday = statToday.getTotalRevenue() != null ? statToday.getTotalRevenue() : BigDecimal.ZERO;
        BigDecimal revYesterday = statYesterday.getTotalRevenue() != null ? statYesterday.getTotalRevenue() : BigDecimal.ZERO;
        double revChange = calculatePercentageChange(revToday.doubleValue(), revYesterday.doubleValue());

        // Orders Today
        int ordersToday = statToday.getTotalOrders() != null ? statToday.getTotalOrders() : 0;
        int ordersYesterday = statYesterday.getTotalOrders() != null ? statYesterday.getTotalOrders() : 0;
        double ordersChange = calculatePercentageChange(ordersToday, ordersYesterday);

        // Active Products
        int activeProducts = statToday.getActiveProductsSnapshot() != null ? statToday.getActiveProductsSnapshot() : 0;
        int activeProducts30DaysAgo = stat30DaysAgo.getActiveProductsSnapshot() != null ? stat30DaysAgo.getActiveProductsSnapshot() : 0;
        double productsChange = calculatePercentageChange(activeProducts, activeProducts30DaysAgo);

        // Total Customers
        int totalCustomers = statToday.getTotalCustomersSnapshot() != null ? statToday.getTotalCustomersSnapshot() : 0;
        int totalCustomers30DaysAgo = stat30DaysAgo.getTotalCustomersSnapshot() != null ? stat30DaysAgo.getTotalCustomersSnapshot() : 0;
        double customersChange = calculatePercentageChange(totalCustomers, totalCustomers30DaysAgo);

        return DashboardKpiResponse.builder()
                .totalRevenue(new DashboardKpiResponse.KpiItem<>(totalRevenue, revChange))
                .ordersToday(new DashboardKpiResponse.KpiItem<>(ordersToday, ordersChange))
                .activeProducts(new DashboardKpiResponse.KpiItem<>(activeProducts, productsChange))
                .totalCustomers(new DashboardKpiResponse.KpiItem<>(totalCustomers, customersChange))
                .build();
    }

    @Override
    public List<RevenueTrendResponse> getRevenueTrend(Integer days) {
        if (days == null || days <= 0) days = 30;
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        LocalDate endDate = LocalDate.now();

        List<DailyStatistic> stats = dailyStatisticRepository.findByStatisticDateBetweenOrderByStatisticDateAsc(startDate, endDate);

        List<RevenueTrendResponse> response = new ArrayList<>();
        // Fill missing days with 0
        for (int i = 0; i < days; i++) {
            LocalDate currentDay = startDate.plusDays(i);
            BigDecimal revenue = stats.stream()
                    .filter(s -> s.getStatisticDate().equals(currentDay))
                    .findFirst()
                    .map(DailyStatistic::getTotalRevenue)
                    .orElse(BigDecimal.ZERO);

            if (revenue == null) revenue = BigDecimal.ZERO;

            response.add(new RevenueTrendResponse(currentDay.toString(), revenue));
        }

        return response;
    }

    @Override
    public List<OrderStatusStatResponse> getOrderStatusStats(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        return orderRepository.countByOrderStatusAndDateRange(start, end);
    }

    @Override
    public List<TopProductResponse> getTopProducts(Integer limit) {
        if (limit == null || limit <= 0) limit = 8;
        
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        
        Pageable topN = PageRequest.of(0, limit);
        List<Object[]> results = orderDetailRepository.findTopSellingProducts(startOfMonth, now, topN);
        
        return results.stream().map(obj -> new TopProductResponse((String) obj[0], ((Number) obj[1]).longValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerGrowthResponse> getCustomerGrowth(Integer months) {
        if (months == null || months <= 0) months = 6;
        
        LocalDate startDate = LocalDate.now().minusMonths(months - 1).withDayOfMonth(1);
        LocalDate endDate = LocalDate.now();
        
        List<DailyStatistic> stats = dailyStatisticRepository.findByStatisticDateBetweenOrderByStatisticDateAsc(startDate, endDate);
        
        List<CustomerGrowthResponse> response = new ArrayList<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
        
        // Group by month
        for (int i = 0; i < months; i++) {
            LocalDate currentMonthStart = startDate.plusMonths(i);
            String monthKey = currentMonthStart.format(monthFormatter);
            
            int newCustomersInMonth = stats.stream()
                    .filter(s -> s.getStatisticDate().getYear() == currentMonthStart.getYear() && s.getStatisticDate().getMonth() == currentMonthStart.getMonth())
                    .mapToInt(s -> s.getNewCustomers() != null ? s.getNewCustomers() : 0)
                    .sum();
            
            response.add(new CustomerGrowthResponse(monthKey, newCustomersInMonth));
        }
        
        return response;
    }

    @Override
    public List<RecentOrderResponse> getRecentOrders(Integer limit) {
        if (limit == null || limit <= 0) limit = 5;
        Pageable topN = PageRequest.of(0, limit);
        return orderRepository.findRecentOrders(topN);
    }

    private double calculatePercentageChange(double current, double previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        double change = ((current - previous) / previous) * 100.0;
        return BigDecimal.valueOf(change).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
