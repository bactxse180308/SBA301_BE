package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CUSTOMER_VOUCHER")
@IdClass(CustomerVoucherId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerVoucher {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;
}
