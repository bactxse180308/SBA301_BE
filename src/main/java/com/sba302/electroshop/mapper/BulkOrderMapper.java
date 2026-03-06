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
    @Mapping(source = "company.companyId", target = "companyId")
    @Mapping(source = "company.companyName", target = "companyName")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "discountCode", target = "discountCode")
    @Mapping(source = "discountPercentage", target = "discountPercentage")
    @Mapping(source = "discountAmount", target = "discountAmount")
    @Mapping(source = "finalPrice", target = "finalPrice")
    @Mapping(source = "discountApplied", target = "discountApplied")
    @Mapping(source = "details", target = "details")
    BulkOrderResponse toResponse(BulkOrder bulkOrder);

    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(target = "appliedTierPrice", ignore = true)
    @Mapping(target = "customizationFee", ignore = true)
    @Mapping(target = "lineTotal", ignore = true)
    BulkOrderDetailResponse toDetailResponse(BulkOrderDetail detail);

    BulkPriceTierResponse toTierResponse(BulkPriceTier tier);

    OrderCustomizationResponse toCustomizationResponse(OrderCustomization customization);
}
