package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequest {

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    private String companyName;

    @NotBlank(message = "Tax code is required")
    @Size(max = 50, message = "Tax code must not exceed 50 characters")
    private String taxCode;

    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String phone;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
}
