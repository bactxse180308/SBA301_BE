package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "status", length = 50)
    private String status;
}
