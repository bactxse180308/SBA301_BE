package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.Wishlist;
import com.sba302.electroshop.repository.WishlistRepository;
import com.sba302.electroshop.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;

    @Override
    public List<Wishlist> findAll() {
        return wishlistRepository.findAll();
    }

    @Override
    public Optional<Wishlist> findById(Integer id) {
        return wishlistRepository.findById(id);
    }

    @Override
    @Transactional
    public Wishlist save(Wishlist wishlist) {
        return wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        wishlistRepository.deleteById(id);
    }
}
