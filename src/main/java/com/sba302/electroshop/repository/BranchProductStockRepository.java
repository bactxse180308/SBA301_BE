package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.BranchProductStock;
import com.sba302.electroshop.entity.BranchProductStockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchProductStockRepository extends JpaRepository<BranchProductStock, BranchProductStockId> {
}
