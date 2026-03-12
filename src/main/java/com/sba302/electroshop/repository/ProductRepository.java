package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    Page<Product> findBySupplier_SupplierId(Integer supplierId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(p) FROM Product p WHERE p.status = 'ACTIVE'")
    Integer countActiveProducts();

    @EntityGraph(attributePaths = {"category", "brand", "supplier"})
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand LEFT JOIN FETCH p.supplier " +
           "WHERE LOWER(p.productName) LIKE :keyword OR LOWER(p.description) LIKE :keyword")
    List<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p " +
           "WHERE LOWER(p.productName) LIKE :keyword OR LOWER(p.description) LIKE :keyword")
    long countSearchByKeyword(@Param("keyword") String keyword);
}
