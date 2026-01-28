package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CreateVoucherRequest;
import com.sba302.electroshop.dto.request.UpdateVoucherRequest;
import com.sba302.electroshop.dto.response.VoucherResponse;
import com.sba302.electroshop.entity.Voucher;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VoucherMapper {

    @Mapping(target = "voucherId", ignore = true)
    Voucher toEntity(CreateVoucherRequest request);

    VoucherResponse toResponse(Voucher voucher);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "voucherId", ignore = true)
    @Mapping(target = "voucherCode", ignore = true)
    void updateEntity(@MappingTarget Voucher entity, UpdateVoucherRequest request);
}
