package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.PaymentTransaction;
import com.sba302.electroshop.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByTxnRef(String txnRef);

    List<PaymentTransaction> findByOrder_OrderId(Integer orderId);

    List<PaymentTransaction> findByOrder_OrderIdAndStatus(Integer orderId, PaymentStatus status);
}

