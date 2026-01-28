package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    @Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    private String productName;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    private Integer categoryId;

    private Integer brandId;

    @Min(value = 0, message = "Quantity must be non-negative")
    private Integer quantity;

    private String status;

    private Integer supplierId;
}
