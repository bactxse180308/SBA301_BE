package com.sba302.electroshop.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VNPayPaymentUrlResponse {

    private String paymentUrl;
    private String txnRef;
    private Integer orderId;
}

