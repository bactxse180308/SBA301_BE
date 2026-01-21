package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BRAND")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Integer brandId;

    @Column(name = "brand_name", nullable = false, unique = true)
    private String brandName;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "description", length = 1000)
    private String description;
}
