package com.sba302.electroshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.sba302.electroshop.entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer>, JpaSpecificationExecutor<Wishlist> {
    Optional<Wishlist> findFirstByUser_UserId(Integer userId);
}
