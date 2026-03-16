package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class CreatePriceTierRequest {

    @NotNull
    private Integer productId;

    @NotNull
    @Min(1)
    private Integer minQty;

    private Integer maxQty;

    @NotNull
    @Positive
    private BigDecimal unitPrice;
}
