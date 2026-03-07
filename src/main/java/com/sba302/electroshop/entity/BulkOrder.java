package com.sba302.electroshop.entity;

import com.sba302.electroshop.enums.BulkOrderStatus;
import jakarta.persistence.*;
import lombok.*;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private BulkOrderStatus status;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "discount_code", length = 50)
    private String discountCode;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "discount_amount", precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "final_price", precision = 19, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "discount_applied")
    @Builder.Default
    private Boolean discountApplied = false;

    @OneToMany(mappedBy = "bulkOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BulkOrderDetail> details = new ArrayList<>();
}
