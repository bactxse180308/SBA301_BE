package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Page<Supplier> findSupplierBySupplierNameContainingIgnoreCase(String keyword, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Supplier s WHERE LOWER(s.supplierName) LIKE :keyword OR LOWER(s.email) LIKE :keyword OR LOWER(s.phoneNumber) LIKE :keyword")
    java.util.List<Supplier> searchByKeyword(@org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);

    boolean existsBySupplierNameIgnoreCase(String supplierName);

    boolean existsBySupplierNameIgnoreCaseAndSupplierIdNot(String supplierName, Integer supplierId);
}
