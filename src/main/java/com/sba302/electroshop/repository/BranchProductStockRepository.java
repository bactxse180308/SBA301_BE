package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.BranchProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BranchProductStockRepository extends JpaRepository<BranchProductStock, Integer>, JpaSpecificationExecutor<BranchProductStock> {
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

    @Query("SELECT COALESCE(SUM(bps.quantity), 0) FROM BranchProductStock bps WHERE bps.product.productId = :productId")
    Integer sumQuantityByProductId(@Param("productId") Integer productId);

    @Query("SELECT bps.product.productId, SUM(bps.quantity) FROM BranchProductStock bps " +
            "WHERE bps.product.productId IN :productIds GROUP BY bps.product.productId")
    java.util.List<Object[]> sumQuantityByProductIds(@Param("productIds") Collection<Integer> productIds);

    @Query("SELECT bps FROM BranchProductStock bps " +
            "JOIN FETCH bps.product " +
            "JOIN FETCH bps.branch " +
            "WHERE bps.product.productId IN :productIds AND bps.quantity > 0")
    List<BranchProductStock> findAllByProductIds(@Param("productIds") Collection<Integer> productIds);

    List<BranchProductStock> findAllByProduct_ProductId(Integer productId);
}
