package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.BulkOrder;
import com.sba302.electroshop.enums.BulkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BulkOrderRepository extends JpaRepository<BulkOrder, Integer>, JpaSpecificationExecutor<BulkOrder> {
    
    long countByStatus(BulkOrderStatus status);

    long countByCreatedAtAfter(LocalDateTime dateTime);

    @Query("SELECT SUM(b.finalPrice) FROM BulkOrder b WHERE b.status IN :paidStatuses AND b.createdAt >= :start AND b.createdAt <= :end")
    BigDecimal sumFinalPriceByStatusInAndCreatedAtBetween(
            @Param("paidStatuses") List<BulkOrderStatus> paidStatuses,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
