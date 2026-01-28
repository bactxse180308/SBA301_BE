package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVoucherRequest {

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal discountValue;

    @Pattern(regexp = "^(PERCENTAGE|FIXED_AMOUNT)$", message = "Discount type must be PERCENTAGE or FIXED_AMOUNT")
    private String discountType;

    private LocalDateTime validFrom;

    @Future(message = "Valid to date must be in the future")
    private LocalDateTime validTo;

    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer usageLimit;
}
