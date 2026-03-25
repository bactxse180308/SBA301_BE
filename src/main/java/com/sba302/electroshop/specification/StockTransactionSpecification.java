package com.sba302.electroshop.specification;

import com.sba302.electroshop.entity.StockTransaction;
import com.sba302.electroshop.enums.TransactionType;
import org.springframework.data.jpa.domain.Specification;

public class StockTransactionSpecification {

    public static Specification<StockTransaction> filter(Integer branchId, TransactionType type, Integer orderId, Integer bulkOrderId) {
        return Specification.where(hasBranchId(branchId))
                .and(hasType(type))
                .and(hasOrderId(orderId))
                .and(hasBulkOrderId(bulkOrderId));
    }

    private static Specification<StockTransaction> hasBranchId(Integer branchId) {
        return (root, query, builder) -> branchId == null ? null : builder.equal(root.get("branch").get("branchId"), branchId);
    }

    private static Specification<StockTransaction> hasType(TransactionType type) {
        return (root, query, builder) -> type == null ? null : builder.equal(root.get("type"), type);
    }

    private static Specification<StockTransaction> hasOrderId(Integer orderId) {
        return (root, query, builder) -> orderId == null ? null : builder.equal(root.get("orderId"), orderId);
    }

    private static Specification<StockTransaction> hasBulkOrderId(Integer bulkOrderId) {
        return (root, query, builder) -> bulkOrderId == null ? null : builder.equal(root.get("bulkOrderId"), bulkOrderId);
    }
}
