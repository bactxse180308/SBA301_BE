package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

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

    @Size(max = 255, message = "Representative name must be less than 255 characters")
    private String representativeName;

    @Size(max = 100, message = "Representative position must be less than 100 characters")
    private String representativePosition;

    @Size(max = 255, message = "Website URL must be less than 255 characters")
    private String website;

    private LocalDate foundingDate;

    @Size(max = 100, message = "Business type must be less than 100 characters")
    private String businessType;

    private Integer employeeCount;

    @Size(max = 255, message = "Industry must be less than 255 characters")
    private String industry;

    @Size(max = 500, message = "Logo URL must be less than 500 characters")
    private String logoUrl;
}
