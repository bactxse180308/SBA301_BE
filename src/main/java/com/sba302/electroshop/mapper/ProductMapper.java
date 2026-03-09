package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CreateProductRequest;
import com.sba302.electroshop.dto.request.UpdateProductRequest;
import com.sba302.electroshop.dto.response.ProductResponse;
import com.sba302.electroshop.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "soldCount", ignore = true)
    Product toEntity(CreateProductRequest request);

    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "category.categoryName", target = "categoryName")
    @Mapping(source = "brand.brandId", target = "brandId")
    @Mapping(source = "brand.brandName", target = "brandName")
    @Mapping(source = "supplier.supplierId", target = "supplierId")
    @Mapping(source = "supplier.supplierName", target = "supplierName")
    @Mapping(target = "imageUrls", expression = "java(mapMediaToUrls(product.getMediaItems()))")
    ProductResponse toResponse(Product product);

    default java.util.List<String> mapMediaToUrls(java.util.List<com.sba302.electroshop.entity.Media> mediaItems) {
        if (mediaItems == null) return null;
        return mediaItems.stream()
                .map(com.sba302.electroshop.entity.Media::getUrl)
                .collect(java.util.stream.Collectors.toList());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "soldCount", ignore = true)
    void updateEntity(@MappingTarget Product entity, UpdateProductRequest request);
}
