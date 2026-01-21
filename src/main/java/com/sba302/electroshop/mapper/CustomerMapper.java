package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CreateCustomerRequest;
import com.sba302.electroshop.dto.response.CustomerResponse;
import com.sba302.electroshop.entity.Customer;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "rewardPoint", constant = "0")
    Customer toEntity(CreateCustomerRequest request);

    CustomerResponse toResponse(Customer customer);
}
