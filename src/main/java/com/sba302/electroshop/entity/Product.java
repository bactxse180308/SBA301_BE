package com.sba302.electroshop.entity;

import com.sba302.electroshop.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PRODUCT")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Nationalized
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Nationalized
    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "price")
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private ProductStatus status;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "original_price")
    private BigDecimal originalPrice;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    @Builder.Default
    @Column(name = "rating")
    private Double rating = 0.0;

    @Builder.Default
    @Column(name = "sold_count")
    private Integer soldCount = 0;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<Media> mediaItems = new java.util.ArrayList<>();

    @Nationalized
    @Column(name = "description_details", columnDefinition = "NVARCHAR(MAX)")
    private String descriptionDetails;

    @Column(name = "main_image", length = 1000)
    private String mainImage;
}
