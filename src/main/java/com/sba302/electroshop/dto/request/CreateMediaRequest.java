package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMediaRequest {

    @NotNull(message = "Product ID is required")
    private Integer productId;

    @NotBlank(message = "Media type is required")
    @Pattern(regexp = "^(IMAGE|VIDEO)$", message = "Media type must be IMAGE or VIDEO")
    private String type;

    @NotBlank(message = "Media URL is required")
    @Size(max = 1000, message = "URL must not exceed 1000 characters")
    private String url;

    @Min(value = 0, message = "Sort order must be non-negative")
    private Integer sortOrder;
}
