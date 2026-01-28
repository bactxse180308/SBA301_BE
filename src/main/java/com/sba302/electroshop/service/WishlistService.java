package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.response.WishlistResponse;

public interface WishlistService {

    WishlistResponse getByUser(Integer userId);

    WishlistResponse addItem(Integer userId, Integer productId);

    void removeItem(Integer userId, Integer productId);

    void clearWishlist(Integer userId);

    boolean isProductInWishlist(Integer userId, Integer productId);
}
