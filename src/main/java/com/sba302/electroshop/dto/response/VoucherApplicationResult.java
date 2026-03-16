package com.sba302.electroshop.dto.response;

import com.sba302.electroshop.entity.UserVoucher;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class VoucherApplicationResult {
    private UserVoucher userVoucher;
    private String voucherCode;
    private String voucherType; // "PERCENT" | "FIXED"
    private BigDecimal discountAmount;
}
