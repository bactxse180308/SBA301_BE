package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.StockTransaction;
import com.sba302.electroshop.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Integer>, JpaSpecificationExecutor<StockTransaction> {

    /**
     * Tìm tất cả transaction của một order theo type.
     * Dùng để kiểm tra đã có EXPORT chưa khi cancel.
     */
    List<StockTransaction> findByOrderIdAndType(Integer orderId, TransactionType type);

    /**
     * Tìm tất cả transaction của một order (mọi type).
     */
    List<StockTransaction> findByOrderId(Integer orderId);
}
