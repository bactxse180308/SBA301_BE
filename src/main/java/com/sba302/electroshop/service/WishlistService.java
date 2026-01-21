package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.Wishlist;
import java.util.List;
import java.util.Optional;

public interface WishlistService {
    List<Wishlist> findAll();

    Optional<Wishlist> findById(Integer id);

    Wishlist save(Wishlist wishlist);

    void deleteById(Integer id);
}
