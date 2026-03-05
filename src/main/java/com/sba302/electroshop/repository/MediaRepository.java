package com.sba302.electroshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sba302.electroshop.entity.Media;
import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Integer> {
    List<Media> findByProduct_ProductIdOrderBySortOrderAsc(Integer productId);

    @Query("SELECT m FROM Media m WHERE m.product.productId = :productId ORDER BY m.sortOrder ASC")
    Optional<Media> findFirstByProductId(@Param("productId") Integer productId);
}
