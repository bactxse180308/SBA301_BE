package com.sba302.electroshop.dto.response;

import com.sba302.electroshop.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionResponse {

    private Long id;
    private String txnRef;
    private Integer orderId;
    private BigDecimal amount;
    private String bankCode;
    private String bankTranNo;
    private String cardType;
    private String responseCode;
    private String transactionNo;
    private String transactionStatus;
    private String payDate;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

