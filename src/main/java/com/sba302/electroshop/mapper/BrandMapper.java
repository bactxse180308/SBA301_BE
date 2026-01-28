package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CreateBrandRequest;
import com.sba302.electroshop.dto.response.BrandResponse;
import com.sba302.electroshop.entity.Brand;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandMapper {

    @Mapping(target = "brandId", ignore = true)
    Brand toEntity(CreateBrandRequest request);

    BrandResponse toResponse(Brand brand);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "brandId", ignore = true)
    void updateEntity(@MappingTarget Brand entity, CreateBrandRequest request);
}
