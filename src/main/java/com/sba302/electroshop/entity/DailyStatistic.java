package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "DAILY_STATISTIC")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "statistic_date", nullable = false, unique = true)
    private LocalDate statisticDate;

    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;

    @Column(name = "total_orders")
    private Integer totalOrders;

    @Column(name = "new_customers")
    private Integer newCustomers;

    @Column(name = "total_customers_snapshot")
    private Integer totalCustomersSnapshot;

    @Column(name = "active_products_snapshot")
    private Integer activeProductsSnapshot;
}
