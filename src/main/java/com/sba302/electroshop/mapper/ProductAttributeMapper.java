package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CreateProductAttributeRequest;
import com.sba302.electroshop.dto.request.UpdateProductAttributeRequest;
import com.sba302.electroshop.dto.response.ProductAttributeResponse;
import com.sba302.electroshop.entity.ProductAttribute;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductAttributeMapper {

    @Mapping(target = "productAttributeId", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "attribute", ignore = true)
    ProductAttribute toEntity(CreateProductAttributeRequest request);

    @Mapping(source = "attribute.attributeId", target = "attributeId")
    @Mapping(source = "attribute.attributeName", target = "attributeName")
    ProductAttributeResponse toResponse(ProductAttribute productAttribute);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "productAttributeId", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "attribute", ignore = true)
    void updateEntity(@MappingTarget ProductAttribute entity, UpdateProductAttributeRequest request);
}
