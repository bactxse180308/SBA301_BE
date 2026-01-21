package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.WishlistItem;
import com.sba302.electroshop.entity.WishlistItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, WishlistItemId> {
}
