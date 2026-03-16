package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdatePriceTierRequest {

    @Min(1)
    private Integer minQty;

    private Integer maxQty;

    @Positive
    private BigDecimal unitPrice;

    private Boolean isActive;
}
