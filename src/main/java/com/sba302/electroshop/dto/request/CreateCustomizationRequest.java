package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomizationRequest {

    @NotBlank(message = "Customization type is required")
    @Size(max = 50, message = "Customization type must not exceed 50 characters")
    private String type;

    @Size(max = 2000, message = "Note must not exceed 2000 characters")
    private String note;

    @DecimalMin(value = "0.0", message = "Extra fee must be non-negative")
    private BigDecimal extraFee;
}
