package com.sba302.electroshop.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarrantyResponse {
    private Integer warrantyId;
    private Integer productId;
    private String productName;
    private Integer warrantyPeriodMonths;
    private String warrantyTerms;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
