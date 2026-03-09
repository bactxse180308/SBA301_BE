package com.sba302.electroshop.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProductResponse {
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private Integer categoryId;
    private String categoryName;
    private Integer brandId;
    private String brandName;
    private Integer quantity;
    private String status;
    private LocalDateTime createdDate;
    private Integer supplierId;
    private String supplierName;
    private BigDecimal originalPrice;
    private Integer discountPercent;
    private Double rating;
    private Integer soldCount;
    private String descriptionDetails;
    private String mainImage;
    private java.util.List<String> imageUrls;
}
