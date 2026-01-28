package com.sba302.electroshop.dto.response;

import lombok.*;
import java.time.LocalDateTime;

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
}
