package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    @org.springframework.data.jpa.repository.Query("SELECT od.product.productName as product, SUM(od.quantity) as sales " +
            "FROM OrderDetail od " +
            "WHERE od.order.orderDate >= :start AND od.order.orderDate <= :end " +
            "GROUP BY od.product.productName " +
            "ORDER BY sales DESC")
    List<Object[]> findTopSellingProducts(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end, org.springframework.data.domain.Pageable pageable);
}
