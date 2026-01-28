package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBrandRequest {

    @NotBlank(message = "Brand name is required")
    @Size(min = 2, max = 255, message = "Brand name must be between 2 and 255 characters")
    private String brandName;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
}
