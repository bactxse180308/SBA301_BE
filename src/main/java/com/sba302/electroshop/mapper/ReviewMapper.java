package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CreateReviewRequest;
import com.sba302.electroshop.dto.request.UpdateReviewRequest;
import com.sba302.electroshop.dto.response.ReviewResponse;
import com.sba302.electroshop.entity.Review;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(target = "reviewId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "reviewDate", ignore = true)
    Review toEntity(CreateReviewRequest request);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    ReviewResponse toResponse(Review review);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "reviewId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "reviewDate", ignore = true)
    void updateEntity(@MappingTarget Review entity, UpdateReviewRequest request);
}
