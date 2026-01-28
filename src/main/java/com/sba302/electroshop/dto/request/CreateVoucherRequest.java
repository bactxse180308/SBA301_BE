package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateVoucherRequest {

    @NotBlank(message = "Voucher code is required")
    @Size(min = 3, max = 100, message = "Voucher code must be between 3 and 100 characters")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Voucher code must contain only uppercase letters, numbers, underscores or hyphens")
    private String voucherCode;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal discountValue;

    @NotBlank(message = "Discount type is required")
    @Pattern(regexp = "^(PERCENTAGE|FIXED_AMOUNT)$", message = "Discount type must be PERCENTAGE or FIXED_AMOUNT")
    private String discountType;

    @NotNull(message = "Valid from date is required")
    private LocalDateTime validFrom;

    @NotNull(message = "Valid to date is required")
    @Future(message = "Valid to date must be in the future")
    private LocalDateTime validTo;

    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer usageLimit;
}
