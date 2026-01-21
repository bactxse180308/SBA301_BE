package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;
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

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "discount_value")
    private BigDecimal discountValue;

    @Column(name = "discount_type", length = 50)
    private String discountType;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @Column(name = "usage_limit")
    private Integer usageLimit;
}
