package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.OrderCustomization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderCustomizationRepository extends JpaRepository<OrderCustomization, Integer> {

    List<OrderCustomization> findByBulkOrderDetail_BulkOrderDetailId(Integer detailId);
}
