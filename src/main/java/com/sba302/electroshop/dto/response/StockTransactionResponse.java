package com.sba302.electroshop.dto.response;

import com.sba302.electroshop.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class StockTransactionResponse {
    private Integer id;
    private TransactionType type;
    private Integer branchId;
    private String branchName;
    private Integer orderId;
    private Integer bulkOrderId;
    private String note;
    private LocalDateTime createdDate;
    
    private List<StockTransactionItemResponse> items;
}
