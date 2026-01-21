package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.ShoppingCart;
import java.util.List;
import java.util.Optional;

public interface ShoppingCartService {
    List<ShoppingCart> findAll();

    Optional<ShoppingCart> findById(Integer id);

    ShoppingCart save(ShoppingCart shoppingCart);

    void deleteById(Integer id);
}
