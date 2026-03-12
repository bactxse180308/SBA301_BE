package com.sba302.electroshop.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sba302.electroshop.entity.Order;
import com.sba302.electroshop.enums.OrderStatus;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {

    @Query("SELECT o FROM Order o JOIN FETCH o.user LEFT JOIN FETCH o.userVoucher uv LEFT JOIN FETCH uv.voucher " +
           "WHERE LOWER(o.user.fullName) LIKE :keyword " +
           "OR LOWER(o.shippingAddress) LIKE :keyword")
    List<Order> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
           "FROM Order o JOIN o.orderDetails od " +
           "WHERE o.user.userId = :userId AND od.product.productId = :productId " +
           "AND o.orderStatus = :status")
    boolean hasUserPurchasedProduct(@Param("userId") Integer userId,
                                    @Param("productId") Integer productId,
                                    @Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :startOfDay AND o.orderDate < :endOfDay")
    Integer countOrdersByDateRange(@Param("startOfDay") java.time.LocalDateTime startOfDay, @Param("endOfDay") java.time.LocalDateTime endOfDay);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate >= :startOfDay AND o.orderDate < :endOfDay AND o.orderStatus = 'DELIVERED'")
    java.math.BigDecimal sumRevenueByDateRange(@Param("startOfDay") java.time.LocalDateTime startOfDay, @Param("endOfDay") java.time.LocalDateTime endOfDay);

    @Query("SELECT new com.sba302.electroshop.dto.response.OrderStatusStatResponse(CAST(o.orderStatus AS string), COUNT(o)) " +
           "FROM Order o " +
           "WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate " +
           "GROUP BY o.orderStatus")
    java.util.List<com.sba302.electroshop.dto.response.OrderStatusStatResponse> countByOrderStatusAndDateRange(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT new com.sba302.electroshop.dto.response.RecentOrderResponse(o.orderId, CAST(o.orderId AS string), o.user.fullName, o.totalAmount, CAST(o.orderStatus AS string), o.orderDate) " +
           "FROM Order o " +
           "ORDER BY o.orderDate DESC")
    java.util.List<com.sba302.electroshop.dto.response.RecentOrderResponse> findRecentOrders(org.springframework.data.domain.Pageable pageable);



}
