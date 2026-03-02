package com.sba302.electroshop.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.sba302.electroshop.entity.Supplier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Page<Supplier> findSupplierBySupplierNameContainingIgnoreCase(String keyword, Pageable pageable);

    boolean existsBySupplierNameIgnoreCase(String supplierName);

    boolean existsBySupplierNameIgnoreCaseAndSupplierIdNot(String supplierName, Integer supplierId);
}
