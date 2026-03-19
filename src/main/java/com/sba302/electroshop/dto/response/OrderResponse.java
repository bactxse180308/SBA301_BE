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
public class OrderResponse {
    private Integer orderId;
    private Integer userId;
    private String userFullName;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String orderStatus;
    private String shippingAddress;
    private String paymentMethod;
    private String paymentStatus;
    private String voucherCode;
    private String cancelReason;
    private List<OrderItemResponse> orderItems;
}
