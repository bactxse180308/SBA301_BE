package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CreateCategoryRequest;
import com.sba302.electroshop.dto.response.CategoryResponse;
import com.sba302.electroshop.entity.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "categoryId", ignore = true)
    Category toEntity(CreateCategoryRequest request);

    CategoryResponse toResponse(Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categoryId", ignore = true)
    void updateEntity(@MappingTarget Category entity, CreateCategoryRequest request);
}
