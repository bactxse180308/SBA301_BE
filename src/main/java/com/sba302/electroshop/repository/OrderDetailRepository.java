package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.OrderDetail;
import com.sba302.electroshop.entity.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
}
