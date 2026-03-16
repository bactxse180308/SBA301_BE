package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.BulkPriceTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BulkPriceTierRepository extends JpaRepository<BulkPriceTier, Integer> {

    List<BulkPriceTier> findByProduct_ProductIdAndIsActiveTrueOrderByMinQtyAsc(Integer productId);

    Optional<BulkPriceTier> findTopByProduct_ProductIdAndMinQtyLessThanEqualAndIsActiveTrueOrderByMinQtyDesc(
            Integer productId, Integer quantity);
}
