package com.sba302.electroshop.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {
    private Integer supplierId;
    private String supplierName;
    private String contactPerson;
    private String email;
    private String phoneNumber;
    private String address;
}
