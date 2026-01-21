package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.Order;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> findAll();

    Optional<Order> findById(Integer id);

    Order save(Order order);

    void deleteById(Integer id);
}
