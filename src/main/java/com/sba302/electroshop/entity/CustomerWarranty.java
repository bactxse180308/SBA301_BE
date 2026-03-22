package com.sba302.electroshop.entity;

import com.sba302.electroshop.enums.CustomerWarrantyStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Entity
@Table(name = "CUSTOMER_WARRANTY", indexes = {
        @Index(name = "idx_cw_user_id",       columnList = "user_id"),
        @Index(name = "idx_cw_product_id",     columnList = "product_id"),
        @Index(name = "idx_cw_order_id",       columnList = "order_id"),
        @Index(name = "idx_cw_bulk_order_id",  columnList = "bulk_order_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWarranty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Nullable: chỉ tồn tại nếu xuất phát từ đơn hàng thông thường.
     * Constraint (CHECK): order_id và bulk_order_id không được có cả hai.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * Nullable: chỉ tồn tại nếu xuất phát từ đơn hàng số lượng lớn.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulk_order_id")
    private BulkOrder bulkOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "warranty_months", nullable = false)
    private Integer warrantyMonths;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private CustomerWarrantyStatus status;

    @Nationalized
    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = CustomerWarrantyStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
