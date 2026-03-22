package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.CustomerWarranty;
import com.sba302.electroshop.enums.CustomerWarrantyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerWarrantyRepository extends JpaRepository<CustomerWarranty, Integer> {

    List<CustomerWarranty> findByUser_UserIdOrderByEndDateDesc(Integer userId);

    @Query("SELECT cw FROM CustomerWarranty cw " +
           "WHERE cw.user.userId = :userId " +
           "  AND cw.status = :status " +
           "  AND cw.endDate >= :now")
    List<CustomerWarranty> findActiveByUserId(
            @Param("userId") Integer userId,
            @Param("status") CustomerWarrantyStatus status,
            @Param("now") LocalDateTime now);

    boolean existsByOrder_OrderIdAndProduct_ProductId(Integer orderId, Integer productId);

    boolean existsByBulkOrder_BulkOrderIdAndProduct_ProductId(Integer bulkOrderId, Integer productId);

    List<CustomerWarranty> findByOrder_OrderId(Integer orderId);

    List<CustomerWarranty> findByBulkOrder_BulkOrderId(Integer bulkOrderId);
}
