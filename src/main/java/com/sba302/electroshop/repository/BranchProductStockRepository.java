package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.BranchProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BranchProductStockRepository extends JpaRepository<BranchProductStock, Integer> {
    boolean existsByProduct_ProductId(Integer productId);


    Integer findQuantityByBranch_BranchIdAndProduct_ProductId(
            Integer branchId,
            Integer productId
    );

    Optional<BranchProductStock> findByBranch_BranchIdAndProduct_ProductId(
            Integer branchId,
            Integer productId
    );


}
