package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Integer> {
    Page<Attribute> findByAttributeNameContainingIgnoreCase(String keyword, Pageable pageable);
}
