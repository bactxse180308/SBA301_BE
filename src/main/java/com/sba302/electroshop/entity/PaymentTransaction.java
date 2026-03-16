package com.sba302.electroshop.entity;

import com.sba302.electroshop.enums.PaymentStatus;
import com.sba302.electroshop.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transaction", indexes = {
        @Index(name = "idx_payment_txn_ref", columnList = "txn_ref"),
        @Index(name = "idx_payment_order_id", columnList = "order_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "txn_ref", length = 100, nullable = false, unique = true)
    private String txnRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulk_order_id")
    private BulkOrder bulkOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", length = 20, nullable = false)
    private PaymentType paymentType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "bank_code", length = 20)
    private String bankCode;

    @Column(name = "bank_tran_no", length = 100)
    private String bankTranNo;

    @Column(name = "card_type", length = 20)
    private String cardType;

    @Column(name = "response_code", length = 10)
    private String responseCode;

    @Column(name = "transaction_no", length = 100)
    private String transactionNo;

    @Column(name = "transaction_status", length = 10)
    private String transactionStatus;

    @Column(name = "pay_date", length = 20)
    private String payDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private PaymentStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "secure_hash", length = 256)
    private String secureHash;
}

