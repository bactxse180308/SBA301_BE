package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.response.OrderItemResponse;
import com.sba302.electroshop.dto.response.OrderResponse;
import com.sba302.electroshop.entity.Order;
import com.sba302.electroshop.entity.OrderDetail;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "userVoucher.voucher.voucherCode", target = "voucherCode")
    @Mapping(source = "orderStatus", target = "orderStatus")
    @Mapping(source = "paymentStatus", target = "paymentStatus")
    @Mapping(source = "orderDetails", target = "orderItems")
    OrderResponse toResponse(Order order);

    @Mapping(source = "orderDetailId", target = "orderDetailId")
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.mainImage", target = "productImage")
    @Mapping(source = "branch.branchName", target = "branchName")
    @Mapping(source = "branch.branchId", target = "branchId")
    OrderItemResponse toItemResponse(OrderDetail orderDetail);
}
