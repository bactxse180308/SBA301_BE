package com.sba302.electroshop.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Integer orderDetailId;
    private Integer productId;
    private String productName;
    private String productImage;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String branchName;
}

