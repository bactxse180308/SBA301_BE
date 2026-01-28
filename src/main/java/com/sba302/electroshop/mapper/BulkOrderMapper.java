package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.response.BulkOrderResponse;
import com.sba302.electroshop.entity.BulkOrder;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BulkOrderMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "status", target = "status")
    BulkOrderResponse toResponse(BulkOrder bulkOrder);
}
