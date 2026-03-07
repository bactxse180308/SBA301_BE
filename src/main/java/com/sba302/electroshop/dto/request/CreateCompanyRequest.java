package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO dùng cho API tạo mới Company.
 * Bao gồm thông tin company và tài khoản User đại diện (role=COMPANY).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompanyRequest {

    // ===== Thông tin công ty =====

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 255, message = "Company name must be between 2 and 255 characters")
    private String companyName;

    @NotBlank(message = "Tax code is required")
    @Size(max = 50, message = "Tax code must not exceed 50 characters")
    private String taxCode;
 
    @NotBlank(message = "Company email is required")
    @Email(message = "Company email must be valid")
    @Size(max = 255, message = "Company email must not exceed 255 characters")
    private String email;
 
    @NotBlank(message = "Phone is required")
    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String phone;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @NotBlank(message = "Representative name is required")
    @Size(max = 255, message = "Representative name must be less than 255 characters")
    private String representativeName;
 
    @NotBlank(message = "Representative position is required")
    @Size(max = 100, message = "Representative position must be less than 100 characters")
    private String representativePosition;

    private LocalDate foundingDate;

    @Size(max = 100, message = "Business type must be less than 100 characters")
    private String businessType;

    private Integer employeeCount;

    @Size(max = 255, message = "Industry must be less than 255 characters")
    private String industry;

    @Size(max = 500, message = "Logo URL must be less than 500 characters")
    private String logoUrl;

    // ===== Thông tin User đại diện công ty (Đã login) =====
 
    @NotNull(message = "User ID is required")
    private Integer userId;
}
