package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CreateStoreBranchRequest;
import com.sba302.electroshop.dto.response.StoreBranchResponse;
import com.sba302.electroshop.entity.StoreBranch;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StoreBranchMapper {

    @Mapping(target = "branchId", ignore = true)
    StoreBranch toEntity(CreateStoreBranchRequest request);

    StoreBranchResponse toResponse(StoreBranch storeBranch);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "branchId", ignore = true)
    void updateEntity(@MappingTarget StoreBranch entity, CreateStoreBranchRequest request);
}
