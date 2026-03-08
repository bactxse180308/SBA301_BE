package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.BranchProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
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

    @Query("SELECT bps FROM BranchProductStock bps " +
            "WHERE bps.branch.branchId IN :branchIds AND bps.product.productId IN :productIds")
    List<BranchProductStock> findAllByBranchIdsAndProductIds(
            @Param("branchIds") Collection<Integer> branchIds,
            @Param("productIds") Collection<Integer> productIds
    );


}
