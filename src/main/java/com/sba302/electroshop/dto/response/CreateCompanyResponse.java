package com.sba302.electroshop.dto.response;

import com.sba302.electroshop.enums.CompanyStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO khi tạo công ty.
 * Trả về toàn bộ thông tin company + thông tin user đại diện được tạo.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompanyResponse {

    // Thông tin công ty
    private Integer companyId;
    private String companyName;
    private String taxCode;
    private String email;
    private String phone;
    private String address;
    private String representativeName;
    private String representativePosition;
    private String website;
    private LocalDate foundingDate;
    private String businessType;
    private Integer employeeCount;
    private String industry;
    private String logoUrl;
    private CompanyStatus status;
    private LocalDateTime approvedAt;

    // Thông tin tài khoản user đại diện vừa tạo
    private Integer userId;
    private String userEmail;
    private String userFullName;
}
