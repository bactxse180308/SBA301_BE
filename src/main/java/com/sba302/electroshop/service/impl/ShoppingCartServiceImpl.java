package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.ShoppingCart;
import com.sba302.electroshop.repository.ShoppingCartRepository;
import com.sba302.electroshop.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public List<ShoppingCart> findAll() {
        return shoppingCartRepository.findAll();
    }

    @Override
    public Optional<ShoppingCart> findById(Integer id) {
        return shoppingCartRepository.findById(id);
    }

    @Override
    @Transactional
    public ShoppingCart save(ShoppingCart shoppingCart) {
        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        shoppingCartRepository.deleteById(id);
    }
}
