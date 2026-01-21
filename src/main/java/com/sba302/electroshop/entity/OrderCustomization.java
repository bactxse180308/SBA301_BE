package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ORDER_CUSTOMIZATION")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCustomization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customization_id")
    private Integer customizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulk_order_detail_id", nullable = false)
    private BulkOrderDetail bulkOrderDetail;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "note", length = 2000)
    private String note;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "extra_fee")
    private BigDecimal extraFee;
}
