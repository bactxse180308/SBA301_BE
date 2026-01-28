package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWarrantyRequest {

    @NotNull(message = "Product ID is required")
    private Integer productId;

    @NotNull(message = "Warranty period is required")
    @Min(value = 1, message = "Warranty period must be at least 1 month")
    @Max(value = 120, message = "Warranty period must not exceed 120 months (10 years)")
    private Integer warrantyPeriodMonths;

    @Size(max = 2000, message = "Warranty terms must not exceed 2000 characters")
    private String warrantyTerms;

    private LocalDateTime startDate;
}
