package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.response.OrderResponse;
import com.sba302.electroshop.entity.Order;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "voucher.voucherCode", target = "voucherCode")
    @Mapping(source = "orderStatus", target = "orderStatus")
    OrderResponse toResponse(Order order);
}
