package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.ProductAttribute;
import com.sba302.electroshop.entity.ProductAttributeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, ProductAttributeId> {
}
