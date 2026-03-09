package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.response.StockCheckResult;
import com.sba302.electroshop.dto.response.StockItemResponse;
import com.sba302.electroshop.entity.BranchProductStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WarehouseMapper {

    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "branch.branchId", target = "branchId")
    @Mapping(source = "branch.branchName", target = "branchName")
    StockItemResponse toStockItemResponse(BranchProductStock stock);

    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "branch.branchId", target = "branchId")
    @Mapping(source = "quantity", target = "availableQuantity")
    StockCheckResult toStockCheckResult(BranchProductStock stock);
}
