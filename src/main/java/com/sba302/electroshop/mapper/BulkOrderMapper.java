package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.response.BulkOrderDetailResponse;
import com.sba302.electroshop.dto.response.BulkOrderResponse;
import com.sba302.electroshop.dto.response.BulkPriceTierResponse;
import com.sba302.electroshop.dto.response.OrderCustomizationResponse;
import com.sba302.electroshop.entity.BulkOrder;
import com.sba302.electroshop.entity.BulkOrderDetail;
import com.sba302.electroshop.entity.BulkPriceTier;
import com.sba302.electroshop.entity.OrderCustomization;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BulkOrderMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "user.phoneNumber", target = "userPhone")
    @Mapping(source = "company.companyId", target = "companyId")
    @Mapping(source = "company.companyName", target = "companyName")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "subtotalAfterTier", target = "subtotalAfterTier")
    @Mapping(source = "voucherCode", target = "voucherCode")
    @Mapping(source = "voucherType", target = "voucherType")
    @Mapping(source = "voucherDiscountAmount", target = "voucherDiscountAmount")
    @Mapping(source = "shippingFee", target = "shippingFee")
    @Mapping(source = "shippingFeeWaived", target = "shippingFeeWaived")
    @Mapping(source = "finalPrice", target = "finalPrice")
    @Mapping(source = "cancelReason", target = "cancelReason")
    @Mapping(source = "shippingAddress", target = "shippingAddress")
    @Mapping(source = "adminNote", target = "adminNote")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "details", target = "details")
    @Mapping(target = "basePriceTotal", ignore = true)
    @Mapping(target = "tierDiscountTotal", ignore = true)
    @Mapping(target = "customizationFeeConfirmed", ignore = true)
    @Mapping(target = "customizationFeePending", ignore = true)
    @Mapping(target = "hasPendingCustomization", ignore = true)
    BulkOrderResponse toResponse(BulkOrder bulkOrder);

    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.mainImage", target = "productImage")
    @Mapping(source = "product.price", target = "basePrice")
    @Mapping(target = "customizationFeeConfirmed", ignore = true)
    @Mapping(target = "customizationFeePending", ignore = true)
    @Mapping(target = "lineTotal", ignore = true)
    @Mapping(target = "tierLabel", ignore = true)
    BulkOrderDetailResponse toDetailResponse(BulkOrderDetail detail);

    BulkPriceTierResponse toTierResponse(BulkPriceTier tier);

    @Mapping(source = "customizationId", target = "customizationId")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "note", target = "note")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "extraFee", target = "extraFee")
    @Mapping(source = "feeType", target = "feeType")
    @Mapping(source = "adminNote", target = "adminNote")
    @Mapping(target = "totalFee", ignore = true)
    OrderCustomizationResponse toCustomizationResponse(OrderCustomization customization);
}
