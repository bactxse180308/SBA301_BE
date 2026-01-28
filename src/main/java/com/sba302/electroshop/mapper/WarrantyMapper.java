package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CreateWarrantyRequest;
import com.sba302.electroshop.dto.response.WarrantyResponse;
import com.sba302.electroshop.entity.Warranty;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WarrantyMapper {

    @Mapping(target = "warrantyId", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    Warranty toEntity(CreateWarrantyRequest request);

    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    WarrantyResponse toResponse(Warranty warranty);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "warrantyId", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateEntity(@MappingTarget Warranty entity, CreateWarrantyRequest request);
}
