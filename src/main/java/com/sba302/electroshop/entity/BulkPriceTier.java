package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "BULK_PRICE_TIER")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkPriceTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bulk_price_tier_id")
    private Integer bulkPriceTierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulk_order_detail_id", nullable = false)
    private BulkOrderDetail bulkOrderDetail;

    @Column(name = "min_qty", nullable = false)
    private Integer minQty;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
}
