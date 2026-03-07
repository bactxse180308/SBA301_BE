package com.sba302.electroshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sba302.electroshop.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
           "FROM Order o JOIN OrderDetail od ON o.orderId = od.order.orderId " +
           "WHERE o.user.userId = :userId AND od.product.productId = :productId " +
           "AND o.orderStatus = 'DELIVERED'")
    boolean hasUserPurchasedProduct(@Param("userId") Integer userId, @Param("productId") Integer productId);

    long countByVoucher_VoucherId(Integer voucherId);
}
