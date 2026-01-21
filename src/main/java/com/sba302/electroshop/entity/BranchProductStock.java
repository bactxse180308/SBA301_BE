package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BRANCH_PRODUCT_STOCK")
@IdClass(BranchProductStockId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchProductStock {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private StoreBranch warehouse;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
