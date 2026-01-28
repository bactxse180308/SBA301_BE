package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.response.AttributeResponse;
import com.sba302.electroshop.entity.Attribute;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttributeMapper {

    @Mapping(target = "attributeId", ignore = true)
    Attribute toEntity(String attributeName);

    AttributeResponse toResponse(Attribute attribute);
}
