package com.sba302.electroshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicSearchResponse {
    private List<ProductDto> products;
    private List<CategoryDto> categories;
    private List<BrandDto> brands;
    private List<VoucherDto> vouchers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDto {
        private Integer productId;
        private String productName;
        private String mainImage;
        private BigDecimal price;
        private Integer discountPercent;
        private Double rating;
        private String categoryName;
        private String brandName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDto {
        private Integer categoryId;
        private String categoryName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandDto {
        private Integer brandId;
        private String brandName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoucherDto {
        private String voucherCode;
        private String description;
        private BigDecimal discountPercent;
    }
}
