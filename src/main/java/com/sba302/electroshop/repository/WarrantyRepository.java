package com.sba302.electroshop.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.sba302.electroshop.dto.response.WarrantyResponse;
import com.sba302.electroshop.entity.Warranty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarrantyRepository extends JpaRepository<Warranty, Integer> {
    Page<Warranty> findByProduct_ProductId(Integer productId, Pageable pageable);
}
