package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.Banner;
import com.sba302.electroshop.enums.BannerPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    Optional<Banner> findById(Long id);

    @Query("SELECT b FROM Banner b WHERE b.isActive = true " +
           "AND (b.startDate IS NULL OR b.startDate <= CURRENT_TIMESTAMP) " +
           "AND (b.endDate IS NULL OR b.endDate >= CURRENT_TIMESTAMP) " +
           "ORDER BY b.sortOrder ASC, b.createdAt DESC")
    List<Banner> findAllActiveAndValid();

    @Query("SELECT b FROM Banner b WHERE b.position = :position " +
           "AND b.isActive = true " +
           "AND (b.startDate IS NULL OR b.startDate <= CURRENT_TIMESTAMP) " +
           "AND (b.endDate IS NULL OR b.endDate >= CURRENT_TIMESTAMP) " +
           "ORDER BY b.sortOrder ASC, b.createdAt DESC")
    List<Banner> findActiveAndValidByPosition(@Param("position") BannerPosition position);

    @Query("SELECT b FROM Banner b WHERE " +
           "(:position IS NULL OR b.position = :position) " +
           "AND (:isActive IS NULL OR b.isActive = :isActive) " +
           "AND (:keyword IS NULL OR b.title LIKE %:keyword% OR b.description LIKE %:keyword%)")
    Page<Banner> findByFilters(
            @Param("position") BannerPosition position,
            @Param("isActive") Boolean isActive,
            @Param("keyword") String keyword,
            Pageable pageable);
}
