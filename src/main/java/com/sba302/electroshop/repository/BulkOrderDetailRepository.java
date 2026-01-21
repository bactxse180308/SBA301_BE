package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.BulkOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulkOrderDetailRepository extends JpaRepository<BulkOrderDetail, Integer> {
}
