package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.AddToCartRequest;
import com.sba302.electroshop.dto.response.CartResponse;

public interface ShoppingCartService {

    CartResponse getByUser(Integer userId);

    CartResponse addItem(Integer userId, AddToCartRequest request);

    CartResponse updateItemQuantity(Integer userId, Integer productId, Integer quantity);

    void removeItem(Integer userId, Integer productId);

    void clearCart(Integer userId);
}
