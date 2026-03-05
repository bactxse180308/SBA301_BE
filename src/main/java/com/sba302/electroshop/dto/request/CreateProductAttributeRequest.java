package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductAttributeRequest {

    @NotNull(message = "Product ID is required")
    private Integer productId;

    @NotNull(message = "Attribute ID is required")
    private Integer attributeId;

    @NotBlank(message = "Value is required")
    private String value;
}
