package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.DailyStatistic;
import com.sba302.electroshop.repository.DailyStatisticRepository;
import com.sba302.electroshop.repository.OrderRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.service.DashboardSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardSyncServiceImpl implements DashboardSyncService {

    private final DailyStatisticRepository dailyStatisticRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void syncDailyStatistics() {
        syncForDate(LocalDate.now());
        // Also sync yesterday to catch any late minute updates
        syncForDate(LocalDate.now().minusDays(1));
    }

    // Run every 15 minutes
    @Scheduled(cron = "0 0/15 * * * ?")
    public void scheduledSync() {
        log.info("Running scheduled dashboard sync...");
        syncDailyStatistics();
        log.info("Scheduled dashboard sync completed.");
    }

    private void syncForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        Integer totalOrders = orderRepository.countOrdersByDateRange(startOfDay, endOfDay);
        if (totalOrders == null) totalOrders = 0;

        BigDecimal totalRevenue = orderRepository.sumRevenueByDateRange(startOfDay, endOfDay);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        Integer newCustomers = userRepository.countNewCustomersByDateRange(startOfDay, endOfDay);
        if (newCustomers == null) newCustomers = 0;

        Integer totalCustomersSnapshot = userRepository.countTotalCustomers();
        if (totalCustomersSnapshot == null) totalCustomersSnapshot = 0;

        Integer activeProductsSnapshot = productRepository.countActiveProducts();
        if (activeProductsSnapshot == null) activeProductsSnapshot = 0;

        DailyStatistic statistic = dailyStatisticRepository.findByStatisticDate(date)
                .orElse(DailyStatistic.builder().statisticDate(date).build());

        statistic.setTotalOrders(totalOrders);
        statistic.setTotalRevenue(totalRevenue);
        statistic.setNewCustomers(newCustomers);
        statistic.setTotalCustomersSnapshot(totalCustomersSnapshot);
        statistic.setActiveProductsSnapshot(activeProductsSnapshot);

        dailyStatisticRepository.save(statistic);
    }
}
