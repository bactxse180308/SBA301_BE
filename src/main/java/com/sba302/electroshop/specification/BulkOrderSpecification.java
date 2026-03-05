package com.sba302.electroshop.specification;

import com.sba302.electroshop.entity.BulkOrder;
import com.sba302.electroshop.enums.BulkOrderStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BulkOrderSpecification {

    public static Specification<BulkOrder> filterBulkOrders(Integer userId, BulkOrderStatus status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("userId"), userId));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

