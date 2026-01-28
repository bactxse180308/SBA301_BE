package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CreateSupplierRequest;
import com.sba302.electroshop.dto.response.SupplierResponse;
import com.sba302.electroshop.entity.Supplier;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {

    @Mapping(target = "supplierId", ignore = true)
    Supplier toEntity(CreateSupplierRequest request);

    SupplierResponse toResponse(Supplier supplier);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "supplierId", ignore = true)
    void updateEntity(@MappingTarget Supplier entity, CreateSupplierRequest request);
}
