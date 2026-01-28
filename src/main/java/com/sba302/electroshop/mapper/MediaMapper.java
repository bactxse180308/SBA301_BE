package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CreateMediaRequest;
import com.sba302.electroshop.dto.response.MediaResponse;
import com.sba302.electroshop.entity.Media;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MediaMapper {

    @Mapping(target = "mediaId", ignore = true)
    @Mapping(target = "product", ignore = true)
    Media toEntity(CreateMediaRequest request);

    @Mapping(source = "product.productId", target = "productId")
    MediaResponse toResponse(Media media);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "mediaId", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateEntity(@MappingTarget Media entity, CreateMediaRequest request);
}
