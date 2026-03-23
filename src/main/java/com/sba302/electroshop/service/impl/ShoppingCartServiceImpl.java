package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.AddToCartRequest;
import com.sba302.electroshop.dto.response.CartResponse;
import com.sba302.electroshop.entity.CartItem;
import com.sba302.electroshop.entity.ShoppingCart;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.repository.CartItemRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.ShoppingCartRepository;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
        ShoppingCart cart = shoppingCartRepository
                .findByUser_UserId(userId)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUser(
                            userRepository.findById(userId)
                                    .orElseThrow(() ->
                                            new ResourceNotFoundException("User not found"))
                    );
                    newCart.setCreatedDate(LocalDateTime.now());
                    return shoppingCartRepository.save(newCart);
                });

        List<CartItem> items = cartItemRepository.findByCart_CartId(cart.getCartId());

        List<CartResponse.CartItemResponse> responses = items.stream().map(item -> {

            BigDecimal price = item.getProduct().getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

            return CartResponse.CartItemResponse.builder()
                    .productId(item.getProduct().getProductId())
                    .productName(item.getProduct().getProductName())
                    .mainImage(item.getProduct().getMainImage())
                    .price(price)
                    .quantity(item.getQuantity())
                    .subtotal(subtotal)
                    .build();

        }).toList();

        BigDecimal total = responses.stream()
                .map(CartResponse.CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getCartId())
                .userId(userId)
                .items(responses)
                .totalAmount(total)
                .totalItems(responses.size())
                .build();
    }

    @Override
    @Transactional
    public CartResponse addItem(Integer userId, AddToCartRequest request) {
        ShoppingCart cart = shoppingCartRepository
                .findByUser_UserId(userId)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUser(userRepository.findById(userId).orElseThrow());
                    newCart.setCreatedDate(LocalDateTime.now());
                    return shoppingCartRepository.save(newCart);
                });

        CartItem item = cartItemRepository
                .findByCart_CartIdAndProduct_ProductId(cart.getCartId(), request.getProductId())
                .orElse(null);

        if (item == null) {

            item = new CartItem();
            item.setCart(cart);
            item.setProduct(
                    productRepository.findById(request.getProductId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Product not found"))
            );
            item.setQuantity(request.getQuantity());
            item.setAddedDate(LocalDateTime.now());

        } else {

            item.setQuantity(item.getQuantity() + request.getQuantity());

        }

        cartItemRepository.save(item);

        return getByUser(userId);
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(Integer userId, Integer productId, Integer quantity) {
        ShoppingCart cart = shoppingCartRepository
                .findByUser_UserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found"));

        CartItem item = cartItemRepository
                .findByCart_CartIdAndProduct_ProductId(cart.getCartId(), productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found"));

        item.setQuantity(quantity);

        cartItemRepository.save(item);

        return getByUser(userId);
    }

    @Override
    @Transactional
    public void removeItem(Integer userId, Integer productId) {
        ShoppingCart cart = shoppingCartRepository
                .findByUser_UserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found"));

        CartItem item = cartItemRepository
                .findByCart_CartIdAndProduct_ProductId(cart.getCartId(), productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found"));

        cartItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void clearCart(Integer userId) {
        ShoppingCart cart = shoppingCartRepository
                .findByUser_UserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found"));

        cartItemRepository.deleteByCart_CartId(cart.getCartId());
    }
}
