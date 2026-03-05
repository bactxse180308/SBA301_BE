package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.BulkOrder;
import com.sba302.electroshop.enums.BulkOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulkOrderRepository extends JpaRepository<BulkOrder, Integer> {
    Page<BulkOrder> findByUserUserId(Integer userId, Pageable pageable);
    Page<BulkOrder> findByStatus(BulkOrderStatus status, Pageable pageable);
    Page<BulkOrder> findByUserUserIdAndStatus(Integer userId, BulkOrderStatus status, Pageable pageable);
}
