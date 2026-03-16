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
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "min_qty", nullable = false)
    private Integer minQty;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "max_qty")
    private Integer maxQty;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Transient
    public Integer getDiscountPercent() {
        if (product != null && product.getPrice() != null && unitPrice != null && product.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diff = product.getPrice().subtract(unitPrice);
            return diff.multiply(new BigDecimal("100"))
                       .divide(product.getPrice(), java.math.RoundingMode.HALF_UP)
                       .intValue();
        }
        return 0;
    }
}
