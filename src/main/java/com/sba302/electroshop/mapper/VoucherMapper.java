package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CreateVoucherRequest;
import com.sba302.electroshop.dto.request.UpdateVoucherRequest;
import com.sba302.electroshop.dto.response.VoucherResponse;
import com.sba302.electroshop.entity.Voucher;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VoucherMapper {

    @Mapping(target = "voucherId", ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isValid", ignore = true)
    Voucher toEntity(CreateVoucherRequest request);

    VoucherResponse toResponse(Voucher voucher);

    @Mapping(target = "userStatus", source = "status")
    @Mapping(target = "voucherId", source = "voucher.voucherId")
    @Mapping(target = "voucherCode", source = "voucher.voucherCode")
    @Mapping(target = "description", source = "voucher.description")
    @Mapping(target = "discountValue", source = "voucher.discountValue")
    @Mapping(target = "discountType", source = "voucher.discountType")
    @Mapping(target = "minOrderValue", source = "voucher.minOrderValue")
    @Mapping(target = "maxDiscount", source = "voucher.maxDiscount")
    @Mapping(target = "usedCount", source = "voucher.usedCount")
    @Mapping(target = "validFrom", source = "voucher.validFrom")
    @Mapping(target = "validTo", source = "voucher.validTo")
    @Mapping(target = "usageLimit", source = "voucher.usageLimit")
    @Mapping(target = "isActive", source = "voucher.isActive")
    VoucherResponse toResponse(com.sba302.electroshop.entity.UserVoucher userVoucher);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "voucherId", ignore = true)
    @Mapping(target = "voucherCode", ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateEntity(@MappingTarget Voucher entity, UpdateVoucherRequest request);
}
