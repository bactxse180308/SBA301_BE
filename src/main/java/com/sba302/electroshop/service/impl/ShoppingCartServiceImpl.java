package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.AddToCartRequest;
import com.sba302.electroshop.dto.response.CartResponse;
import com.sba302.electroshop.repository.CartItemRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.ShoppingCartRepository;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public CartResponse getByUser(Integer userId) {
        // TODO: Implement - find cart by user, map to response with items
        return null;
    }

    @Override
    @Transactional
    public CartResponse addItem(Integer userId, AddToCartRequest request) {
        // TODO: Implement - add item to cart or update quantity if exists
        return null;
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(Integer userId, Integer productId, Integer quantity) {
        // TODO: Implement - update item quantity in cart
        return null;
    }

    @Override
    @Transactional
    public void removeItem(Integer userId, Integer productId) {
        // TODO: Implement - remove item from cart
    }

    @Override
    @Transactional
    public void clearCart(Integer userId) {
        // TODO: Implement - clear all items from user's cart
    }
}
