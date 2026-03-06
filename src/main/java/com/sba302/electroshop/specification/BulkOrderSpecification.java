package com.sba302.electroshop.specification;

import com.sba302.electroshop.entity.BulkOrder;
import com.sba302.electroshop.enums.BulkOrderStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BulkOrderSpecification {

    public static Specification<BulkOrder> filterBulkOrders(
            Integer userId,
            Integer companyId,
            BulkOrderStatus status,
            LocalDateTime createdAtFrom,
            LocalDateTime createdAtTo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("userId"), userId));
            }

            if (companyId != null) {
                predicates.add(cb.equal(root.get("company").get("companyId"), companyId));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (createdAtFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtFrom));
            }

            if (createdAtTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdAtTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

