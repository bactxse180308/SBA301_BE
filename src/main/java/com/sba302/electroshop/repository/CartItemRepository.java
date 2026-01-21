package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.CartItem;
import com.sba302.electroshop.entity.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
}
