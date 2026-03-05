package com.sba302.electroshop.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOrderResponse {
    private Integer bulkOrderId;
    private Integer userId;
    private String userFullName;
    private LocalDateTime createdAt;
    private String status;
    private BigDecimal totalPrice;
    private List<BulkOrderDetailResponse> details;
}
