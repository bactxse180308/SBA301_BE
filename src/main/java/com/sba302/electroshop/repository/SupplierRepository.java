package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Page<Supplier> findSupplierBySupplierNameIgnoreCase(String keyword, Pageable pageable);

    Page<Supplier> findSupplierBySupplierNameContainingIgnoreCase(String keyword, Pageable pageable);

    boolean existsBySupplierNameIgnoreCase(String supplierName);
}
