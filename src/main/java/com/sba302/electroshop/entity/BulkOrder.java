package com.sba302.electroshop.entity;

import com.sba302.electroshop.enums.BulkOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BULK_ORDER")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bulk_order_id")
    private Integer bulkOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private BulkOrderStatus status;

    @Column(name = "subtotal_after_tier", precision = 19, scale = 2)
    private BigDecimal subtotalAfterTier;

    @Column(name = "voucher_code", length = 50)
    private String voucherCode;

    @Column(name = "voucher_type", length = 20)
    private String voucherType;

    @Column(name = "voucher_discount_amount", precision = 19, scale = 2)
    private BigDecimal voucherDiscountAmount;

    @Column(name = "shipping_fee", precision = 19, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "shipping_fee_waived")
    @Builder.Default
    private Boolean shippingFeeWaived = false;

    @Column(name = "final_price", precision = 19, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @Nationalized
    @Column(name = "shipping_address", length = 1000)
    private String shippingAddress;

    @Nationalized
    @Column(name = "admin_note", length = 2000)
    private String adminNote;

    @Column(name = "discount_applied")
    @Builder.Default
    private Boolean discountApplied = false;

    @OneToMany(mappedBy = "bulkOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BulkOrderDetail> details = new ArrayList<>();
}
