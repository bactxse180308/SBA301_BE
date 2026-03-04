package com.sba302.electroshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sba302.electroshop.entity.WishlistItem;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Integer>, JpaSpecificationExecutor<WishlistItem> {
    @Modifying
    @Query("DELETE FROM WishlistItem wi WHERE wi.wishlist.wishlistId = :wishlistId")
    void deleteByWishlistId(@Param("wishlistId") Integer wishlistId);
}
