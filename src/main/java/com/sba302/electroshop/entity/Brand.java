package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

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

    @Nationalized
    @Column(name = "brand_name", nullable = false, unique = true)
    private String brandName;

    @Column(name = "country", length = 100)
    private String country;

    @Nationalized
    @Column(name = "description", length = 1000)
    private String description;
}
