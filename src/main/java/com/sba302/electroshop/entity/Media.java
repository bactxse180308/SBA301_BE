package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEDIA")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_id")
    private Integer mediaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
