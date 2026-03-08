package com.sba302.electroshop.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherResponse {
    private Integer voucherId;
    private String voucherCode;
    private String description;
    private BigDecimal discountValue;
    private String discountType;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;
    private Integer usedCount;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Integer usageLimit;
    private Boolean isActive;
    private Boolean isValid;
}
