package com.sba302.electroshop.dto.response;

import lombok.*;

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
}
