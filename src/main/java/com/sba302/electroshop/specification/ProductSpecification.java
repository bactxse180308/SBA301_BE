package com.sba302.electroshop.specification;

import com.sba302.electroshop.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filter(String keyword, Integer categoryId, Integer brandId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String searchPattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), searchPattern));
            }

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("categoryId"), categoryId));
            }

            if (brandId != null) {
                predicates.add(criteriaBuilder.equal(root.get("brand").get("brandId"), brandId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
