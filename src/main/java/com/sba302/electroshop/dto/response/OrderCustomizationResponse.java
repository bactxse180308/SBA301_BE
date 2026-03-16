package com.sba302.electroshop.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCustomizationResponse {
    private Integer customizationId;
    private String type;
    private String note;
    private String status;
    private BigDecimal extraFee;
    private String feeType;
    private BigDecimal totalFee;
    private String adminNote;
}

