package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.response.WishlistResponse;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.repository.WishlistItemRepository;
import com.sba302.electroshop.repository.WishlistRepository;
import com.sba302.electroshop.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public WishlistResponse getByUser(Integer userId) {
        // TODO: Implement - find wishlist by user, map to response with items
        return null;
    }

    @Override
    @Transactional
    public WishlistResponse addItem(Integer userId, Integer productId) {
        // TODO: Implement - add product to user's wishlist
        return null;
    }

    @Override
    @Transactional
    public void removeItem(Integer userId, Integer productId) {
        // TODO: Implement - remove product from wishlist
    }

    @Override
    @Transactional
    public void clearWishlist(Integer userId) {
        // TODO: Implement - clear all items from wishlist
    }

    @Override
    public boolean isProductInWishlist(Integer userId, Integer productId) {
        // TODO: Implement - check if product is in wishlist
        return false;
    }
}
