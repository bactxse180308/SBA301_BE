package com.sba302.electroshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentOrderResponse {
    private Integer id;
    private String orderCode;
    private String customer;
    private BigDecimal amount;
    private String status;
    private LocalDateTime date;
}
