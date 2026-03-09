package com.sba302.electroshop.specification;

import com.sba302.electroshop.entity.BranchProductStock;
import com.sba302.electroshop.entity.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class WarehouseSpecification {

    public static Specification<BranchProductStock> filter(String keyword, Integer branchId) {
        return (root, query, cb) -> {
            // Fix N+1: Fetch product and branch only if this is not a count query
            if (Long.class != query.getResultType()) {
                root.fetch("product", JoinType.LEFT);
                root.fetch("branch", JoinType.LEFT);
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (branchId != null) {
                predicates.add(cb.equal(root.get("branch").get("branchId"), branchId));
            }

            if (keyword != null && !keyword.isBlank()) {
                // Reuse existing fetch if possible or use join for filtering
                Join<BranchProductStock, Product> productJoin = root.join("product");
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(productJoin.get("productName")), pattern));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
