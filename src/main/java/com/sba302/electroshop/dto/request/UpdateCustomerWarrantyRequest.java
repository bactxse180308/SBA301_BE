package com.sba302.electroshop.dto.request;

import com.sba302.electroshop.enums.CustomerWarrantyStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCustomerWarrantyRequest {

    private String notes;
    private CustomerWarrantyStatus status;
}
