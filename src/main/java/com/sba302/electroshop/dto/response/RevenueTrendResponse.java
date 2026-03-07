package com.sba302.electroshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueTrendResponse {
    private String day; // "YYYY-MM-DD"
    private BigDecimal revenue;
}
