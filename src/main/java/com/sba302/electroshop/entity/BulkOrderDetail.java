package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "BULK_ORDER_DETAILS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bulk_order_detail_id")
    private Integer bulkOrderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulk_order_id", nullable = false)
    private BulkOrder bulkOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price_snapshot")
    private BigDecimal unitPriceSnapshot;

    @Column(name = "discount_snapshot")
    private BigDecimal discountSnapshot;
}
