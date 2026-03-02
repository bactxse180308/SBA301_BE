package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findBySupplier_SupplierId(Integer supplierId, Pageable pageable);
}
