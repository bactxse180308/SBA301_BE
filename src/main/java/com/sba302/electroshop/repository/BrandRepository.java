package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Page<Brand> findByBrandNameContainingIgnoreCase(String keyword, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT b FROM Brand b WHERE LOWER(b.brandName) LIKE :keyword")
    java.util.List<Brand> searchByKeyword(@org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);
}
