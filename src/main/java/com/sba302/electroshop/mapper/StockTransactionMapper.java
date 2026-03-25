package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.response.StockTransactionItemResponse;
import com.sba302.electroshop.dto.response.StockTransactionResponse;
import com.sba302.electroshop.entity.StockTransaction;
import com.sba302.electroshop.entity.StockTransactionItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class StockTransactionMapper {

    public StockTransactionResponse toResponse(StockTransaction entity) {
        if (entity == null) {
            return null;
        }

        return StockTransactionResponse.builder()
                .id(entity.getId())
                .type(entity.getType())
                .branchId(entity.getBranch() != null ? entity.getBranch().getBranchId() : null)
                .branchName(entity.getBranch() != null ? entity.getBranch().getBranchName() : null)
                .orderId(entity.getOrderId())
                .bulkOrderId(entity.getBulkOrderId())
                .note(entity.getNote())
                .createdDate(entity.getCreatedDate())
                .items(entity.getItems() != null ? entity.getItems().stream()
                        .map(this::toItemResponse).collect(Collectors.toList()) : null)
                .build();
    }

    private StockTransactionItemResponse toItemResponse(StockTransactionItem entity) {
        if (entity == null) {
            return null;
        }

        return StockTransactionItemResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct() != null ? entity.getProduct().getProductId() : null)
                .productName(entity.getProduct() != null ? entity.getProduct().getProductName() : null)
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .build();
    }
}
