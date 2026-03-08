package com.sba302.electroshop.entity;

import com.sba302.electroshop.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "VOUCHER")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private Integer voucherId;

    @Column(name = "voucher_code", nullable = false, unique = true, length = 100)
    private String voucherCode;

    @Nationalized
    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "discount_value")
    private BigDecimal discountValue;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private BigDecimal minOrderValue;

    private BigDecimal maxDiscount;

    private Integer usedCount;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "is_active")
    private Boolean isActive = true;

}
