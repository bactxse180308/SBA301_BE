package com.sba302.electroshop.dto.response;

import com.sba302.electroshop.enums.CompanyStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {
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

    private CompanyStatus status;
    private String logoUrl;
    private LocalDateTime approvedAt;
}
