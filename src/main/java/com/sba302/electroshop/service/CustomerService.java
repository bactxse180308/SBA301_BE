package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> findAll();

    Optional<Customer> findById(Integer id);

    Customer save(Customer customer);

    void deleteById(Integer id);
}
